package com.mobi.ripple_be.chat.http.service;

import com.mobi.ripple_be.chat.entity.postgres.Chat;
import com.mobi.ripple_be.chat.entity.postgres.ChatUser;
import com.mobi.ripple_be.chat.http.model.ChatModel;
import com.mobi.ripple_be.chat.http.model.MessageModel;
import com.mobi.ripple_be.chat.repository.mongo.MessageRepository;
import com.mobi.ripple_be.chat.repository.mongo.PendingMessagesUsersRepository;
import com.mobi.ripple_be.chat.repository.postgres.ChatRepository;
import com.mobi.ripple_be.chat.repository.postgres.ChatUserRepository;
import com.mobi.ripple_be.chat.repository.postgres.ChatsMessagesRepository;
import com.mobi.ripple_be.chat.websocket.util.ChatEventType;
import com.mobi.ripple_be.chat.websocket.util.ChatNotificationTrigger;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.service.PathService;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import com.mobi.ripple_be.util.Authorable;
import com.mobi.ripple_be.view.AppUserView;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ChatService implements Authorable {

    @Value("${ripple.page-size.chats}")
    private Integer CHATS_PAGE_SIZE;

    @Value("${ripple.page-size.messages}")
    private Integer MESSAGES_PAGE_SIZE;


    private final MessageRepository messageRepository;
    private final ChatsMessagesRepository chatsMessagesRepository;

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final PendingMessagesUsersRepository pendingMessagesUsersRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final PathService pathService;

    private final ChatNotificationTrigger eventTrigger;

    private final DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    public Mono<ChatModel> createNewChat(ChatModel chatModel) {

        Function<String, Mono<ChatModel>> createChat = chatName ->
                chatRepository.save(new Chat(chatName, Instant.now()))
                        .flatMap(chat -> AuthPrincipalProvider
                                .getAuthenticatedUserUUIDMono()
                                .flatMap(userId -> {
                                    List<ChatUser> chatUserList = new ArrayList<>();
                                    for (String participantId : chatModel.getParticipantIds()) {
                                        chatUserList.add(new ChatUser(chat.getId(), UUID.fromString(participantId)));
                                    }
                                    chatUserList.add(new ChatUser(chat.getId(), userId));
                                    return eventTrigger.triggerNotification(
                                                    ChatEventType.CHAT_CREATED,
                                                    chat.getId().toString()
                                            )
                                            .then(chatUserRepository.saveAll(chatUserList)
                                                    .then(Mono.just(
                                                            Objects.requireNonNull(conversionService.convert(chat, ChatModel.class))
                                                    ))
                                            );
                                })
                        );
        String chatName = chatModel.getName();
        if (chatName == null) {
            if (chatModel.getParticipantIds().size() > 1) {
                chatName = "New group of " + (chatModel.getParticipantIds().size() + 1) + " members";
                return createChat.apply(chatName);
            } else {
                return userRepository.findById(UUID.fromString(chatModel.getParticipantIds().getFirst()))
                        .flatMap(user -> {
                            if (user.getFullName() != null) {
                                return createChat.apply(user.getFullName());
                            } else return createChat.apply(user.getUsername());
                        });
            }
        }
        return createChat.apply(chatName);
    }

    public Flux<ChatModel> getChats(int page) {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flux()
                .flatMap(userId -> chatRepository.getUserChatsPage(
                        userId, page * CHATS_PAGE_SIZE, CHATS_PAGE_SIZE
                ))
                .mapNotNull(chat -> conversionService.convert(chat, ChatModel.class));
    }

    @PreAuthorize("@chatService.isAuthorized(#chatId)")
    public Flux<MessageModel> getMessages(UUID chatId, int page) {
        return chatsMessagesRepository.findByChatId(
                        chatId, MESSAGES_PAGE_SIZE, (long) page * MESSAGES_PAGE_SIZE
                )
                .flatMap(chatsMessages -> messageRepository.findById(chatsMessages.getMsgId()))
                .mapNotNull(message -> conversionService.convert(message, MessageModel.class));
    }

    public Mono<Boolean> hasPendingMessages() {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .map(pendingMessagesUsersRepository::findByUserId)
                .hasElement();
    }

    @PreAuthorize("@chatService.isAuthorized(#chatId)")
    public Flux<AppUserView> getAllChatParticipants(String chatId) {
        return chatUserRepository.findAllByChatId(UUID.fromString(chatId))
                .flatMap(chatUser -> userRepository.getAppUserViewById(chatUser.getUserId()));
    }

    @PreAuthorize("@chatService.isAuthorized(#chatId)")
    public Mono<Flux<DataBuffer>> getMessageFile(String chatId, String filename) {
        return Mono.fromCallable(() ->
                DataBufferUtils.read(
                        pathService.getChatFilePath(chatId).resolve(filename),
                        dataBufferFactory,
                        20 * 1024 * 1024
                )
        );
    }

    @Override
    public Mono<Boolean> isAuthorized(String chatId) {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userId -> chatUserRepository
                        .findByChatIdAndUserId(UUID.fromString(chatId), userId)
                        .hasElement()
                );
    }
}
