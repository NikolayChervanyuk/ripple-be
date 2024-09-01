package com.mobi.ripple_be.chat.http.service;

import com.mobi.ripple_be.chat.entity.postgres.Chat;
import com.mobi.ripple_be.chat.entity.postgres.ChatUser;
import com.mobi.ripple_be.chat.http.model.ChatModel;
import com.mobi.ripple_be.chat.repository.postgres.ChatRepository;
import com.mobi.ripple_be.chat.repository.postgres.ChatUserRepository;
import com.mobi.ripple_be.chat.websocket.util.ChatEventType;
import com.mobi.ripple_be.chat.websocket.util.ChatNotificationTrigger;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.service.PathService;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import com.mobi.ripple_be.view.AppUserView;
import io.netty.buffer.ByteBufAllocator;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final PathService pathService;

    private final ChatNotificationTrigger eventTrigger;

    private final DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    public Mono<ChatModel> createNewChat(ChatModel chatModel) {

        Function<String, Mono<ChatModel>> createChat = chatName ->
                chatRepository.save(new Chat(chatName))
                        .flatMap(chat -> AuthPrincipalProvider
                                .getAuthenticatedUserUUIDMono()
                                .flatMap(userId -> {
                                    List<ChatUser> chatUserList = new ArrayList<>();
                                    for (String participantId : chatModel.getParticipantIds()) {
                                        chatUserList.add(new ChatUser(chat.getId(), UUID.fromString(participantId)));
                                    }
                                    chatUserList.add(new ChatUser(chat.getId(), userId));
                                    return eventTrigger.triggerNotification(ChatEventType.CHAT_CREATED, chat.getId().toString())
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
                        .flatMap(user -> createChat.apply(user.getFullName()));
            }
        }
        return createChat.apply(chatName);
    }

    public Flux<AppUserView> getAllChatParticipants(String chatId) {
        return chatUserRepository.findAllByChatId(UUID.fromString(chatId))
                .flatMap(chatUser -> userRepository.getAppUserViewById(chatUser.getUserId()));
    }

    public Mono<Flux<DataBuffer>> getMessageFile(String chatId, String filename) {
        return Mono.fromCallable(() ->
                DataBufferUtils.read(
                        pathService.getChatFilePath(chatId).resolve(filename),
                        dataBufferFactory,
                        16 * 1024
                )
        );
    }


}
