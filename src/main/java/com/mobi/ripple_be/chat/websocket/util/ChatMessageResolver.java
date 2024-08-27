package com.mobi.ripple_be.chat.websocket.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mobi.ripple_be.chat.entity.mongo.Message;
import com.mobi.ripple_be.chat.entity.mongo.PendingMessageUser;
import com.mobi.ripple_be.chat.entity.postgres.ChatUser;
import com.mobi.ripple_be.chat.exception.UnresolvedMessageException;
import com.mobi.ripple_be.chat.repository.mongo.MessageRepository;
import com.mobi.ripple_be.chat.repository.mongo.PendingMessageUserRepository;
import com.mobi.ripple_be.chat.repository.postgres.ChatUserRepository;
import com.mobi.ripple_be.chat.websocket.dto.GenericMessageDTO;
import com.mobi.ripple_be.chat.websocket.dto.content.*;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.NewMessageContentDTO;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.NewParticipantContentDTO;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.ParticipantLeftContentDTO;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.ParticipantRemovedContentDTO;
import com.mobi.ripple_be.chat.websocket.dto.message.*;
import com.mobi.ripple_be.exception.ApplicationException;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
@Component
public class ChatMessageResolver {

    private final ChatObjectMapper chatObjectMapper;
    private final MessageRepository messageRepository;
    private final PendingMessageUserRepository pendingMessageUserRepository;
    private final UserRepository userRepository;
    private final ChatUserRepository chatUserRepository;

    public ResolvedMessage resolve(WebSocketMessage webSocketMessage) {
        try (InputStream chatMessageStream = webSocketMessage.getPayload().asInputStream()) {
            JsonNode jsonNode = chatObjectMapper.readTree(chatMessageStream.readAllBytes());
            String eventType = jsonNode.get("eventType").textValue();
            if (eventType == null) {
                throw new ApplicationException("eventType can't be null");
            }

            if (eventType.equals(ChatEventType.NEW_MESSAGE.getLiteral())) {
                return new ResolvedMessage(
                        chatObjectMapper.convertValue(jsonNode, NewMessageDTO.class),
                        ChatEventType.NEW_MESSAGE
                );
            } else if (eventType.equals(ChatEventType.CHAT_OPENED.getLiteral())) {
                return new ResolvedMessage(
                        chatObjectMapper.convertValue(jsonNode, ChatOpenedDTO.class),
                        ChatEventType.CHAT_OPENED
                );
            } else if (eventType.equals(ChatEventType.CHAT_CREATED.getLiteral())) {
                return new ResolvedMessage(
                        chatObjectMapper.convertValue(jsonNode, ChatCreatedDTO.class),
                        ChatEventType.CHAT_CREATED
                );
            }
//            else if (eventType.equals(ChatEventType.USER_ONLINE.getLiteral())) {
//                return new ResolvedMessage(
//                        chatObjectMapper.convertValue(jsonNode, UserOnlineDTO.class),
//                        ChatEventType.USER_ONLINE
//                );
//            }
            else if (eventType.equals(ChatEventType.NEW_PARTICIPANT.getLiteral())) {
                return new ResolvedMessage(
                        chatObjectMapper.convertValue(jsonNode, NewParticipantDTO.class),
                        ChatEventType.NEW_PARTICIPANT
                );
            } else if (eventType.equals(ChatEventType.PARTICIPANT_LEFT.getLiteral())) {
                return new ResolvedMessage(
                        chatObjectMapper.convertValue(jsonNode, ParticipantLeftDTO.class),
                        ChatEventType.PARTICIPANT_LEFT
                );
            } else if (eventType.equals(ChatEventType.PARTICIPANT_REMOVED.getLiteral())) {
                return new ResolvedMessage(
                        chatObjectMapper.convertValue(jsonNode, ParticipantRemovedDTO.class),
                        ChatEventType.PARTICIPANT_REMOVED
                );
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UnresolvedMessageException();
        }
        throw new UnresolvedMessageException();
    }

    public Mono<Void> sendToChatRecipients(final Message storedMessage,
                                           final ChatEventType eventType,
                                           final HashMap<UUID, WebSocketSession> sessionHashMap
    ) {
        var message = new ResolvedMessage(null, eventType);

        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userId -> message.sendToChatRecipients(storedMessage, userId, sessionHashMap));
    }

    @Getter
    @SuppressWarnings("rawtypes")
    public class ResolvedMessage {

        private final GenericMessageDTO messageDTO;
        private final ChatEventType eventType;

        private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

        private final HashMap<ChatEventType, Function<ActionArguments, Mono<Message>>> storeActions = new HashMap<>();

        public ResolvedMessage(GenericMessageDTO messageDTO, ChatEventType eventType) {
            this.messageDTO = messageDTO;
            this.eventType = eventType;
            initStoreActions();
        }

        public Mono<Message> storeMessage(UUID senderId) {
            return storeActions.get(eventType).apply(new ActionArguments(messageDTO, senderId));
        }

        public Mono<Void> sendToChatRecipients(
                Message storedMessage,
                UUID senderId,
                final HashMap<UUID, WebSocketSession> sessionHashMap
        ) {
            try {
                final Method getChatIdMethod =
                        storedMessage.getMessageContent().getClass().getMethod("getChatId");
                String chatId = (String) getChatIdMethod.invoke(storedMessage.getMessageContent());

                return userRepository.findAppUserViewsFromChat(UUID.fromString(chatId))
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(userView -> {
                            var foundUserSession = sessionHashMap.get(userView.getId());
                            if (foundUserSession == null) {
                                return pendingMessageUserRepository.save(PendingMessageUser.builder()
                                        .userId(userView.getId().toString())
                                        .msgId(storedMessage.getMsgId())
                                        .build()
                                ).then();
                            } else {
                                if (!userView.getId().equals(senderId)) {
                                    try {
                                        return foundUserSession.send(Mono.just(
                                                new WebSocketMessage(
                                                        WebSocketMessage.Type.TEXT,
                                                        getBufferWithJsonMessage(storedMessage)
                                                )
                                        ));
                                    } catch (JsonProcessingException e) {
                                        return Flux.error(new RuntimeException(e));
                                    }
                                }
                                return Mono.empty();
                            }
                        }).then(Mono.empty());
            } catch (NoSuchMethodException e) {
                log.error("Message does not have a specified 'chatId' " +
                        "field so message can't be routed to any chat participants");
                return Mono.empty();
            } catch (InvocationTargetException e) {
                throw new ApplicationException(
                        "This exception should not happen, something is wrong with 'getChatId'?"
                );
            } catch (IllegalAccessException e) {
                throw new ApplicationException("Can't invoke 'getChatId' method'");
            }

        }

        @SneakyThrows
        private DataBuffer getBufferWithJsonMessage(Message messageToSerialize) throws JsonProcessingException {
            //TODO: Make more readable
            //TODO: when client sends new massage, it should create the file via http POST request,
            // then in new message should be stored the name of the file, so when connected users
            // can retrieve it via http GET. That is because max frame length of message via websocket is 65KB
            final byte[] jsonValBytes;
            jsonValBytes = chatObjectMapper.writeValueAsString(messageToSerialize).getBytes();
            DataBuffer buffer = bufferFactory.allocateBuffer(jsonValBytes.length);
            return buffer.write(jsonValBytes);
        }

        private record ActionArguments(GenericMessageDTO messageDTO, UUID senderId) {
        }

        private void initStoreActions() {
            storeActions.put(ChatEventType.NEW_MESSAGE,
                    (actionArguments) -> {
                        final NewMessageDTO message = (NewMessageDTO) actionArguments.messageDTO;
                        final NewMessageContentDTO content = message.getContent();
                        return messageRepository.save(Message.builder()
                                .eventType(ChatEventType.NEW_MESSAGE)
                                .sentDate(Instant.now())
                                .messageContent(NewMessageContent.builder()
                                        .senderId(actionArguments.senderId.toString())
                                        .chatId(content.getChatId())
                                        .message(content.getMessage())
                                        .fileName(content.getFileName())
                                        .fileExtension(content.getFileName() != null ? content.getFileExtension() : null)
                                        .build()
                                ).build()
                        );
                    }
            );
            storeActions.put(ChatEventType.CHAT_OPENED,
                    (actionArguments) -> {
                        final ChatOpenedDTO message = (ChatOpenedDTO) actionArguments.messageDTO;
                        return messageRepository.save(Message.builder()
                                .eventType(ChatEventType.CHAT_OPENED)
                                .sentDate(Instant.now())
                                .messageContent(
                                        ChatOpenedContent.builder()
                                                .userId(actionArguments.senderId.toString())
                                                .chatId(message.getContent().getChatId())
                                                .build()
                                ).build()
                        );
                    }
            );
            storeActions.put(ChatEventType.CHAT_CREATED,
                    (actionArguments) -> {
                        final ChatCreatedDTO message = (ChatCreatedDTO) actionArguments.messageDTO;
                        return messageRepository.save(Message.builder()
                                .eventType(ChatEventType.CHAT_CREATED)
                                .sentDate(Instant.now())
                                .messageContent(
                                        ChatCreatedContent.builder()
                                                .creatorId(actionArguments.senderId.toString())
                                                .chatId(message.getContent().getChatId())
                                                .build()
                                ).build()
                        );
                    }
            );
//            storeActions.put(ChatEventType.USER_ONLINE,
//                    (actionArguments) -> {
//                        return messageRepository.save(Message.builder()
//                                .eventType(ChatEventType.USER_ONLINE)
//                                .sentDate(Instant.now())
//                                .messageContent(null)
//                                .build()
//                        );
//                    }
//            );
            //TODO: reflect the action on database
            storeActions.put(ChatEventType.NEW_PARTICIPANT,
                    (actionArguments) -> {
                        final NewParticipantDTO message = (NewParticipantDTO) actionArguments.messageDTO;
                        final var content = (NewParticipantContentDTO) message.getContent();
                        return chatUserRepository.save(
                                new ChatUser(
                                        UUID.fromString(content.getChatId()),
                                        UUID.fromString(content.getParticipantId())
                                )
                        ).then(messageRepository.save(Message.builder()
                                .eventType(ChatEventType.NEW_PARTICIPANT)
                                .sentDate(Instant.now())
                                .messageContent(NewParticipantContent.builder()
                                        .inviterId(actionArguments.senderId.toString())
                                        .chatId(message.getContent().getChatId())
                                        .participantId(message.getContent().getParticipantId())
                                        .build()
                                ).build()
                        ));
                    }
            );
            storeActions.put(ChatEventType.PARTICIPANT_LEFT,
                    (actionArguments) -> {
                        final ParticipantLeftDTO message = (ParticipantLeftDTO) actionArguments.messageDTO;
                        final var content = (ParticipantLeftContentDTO) message.getContent();

                        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                                .flatMap(userId ->
                                        chatUserRepository
                                                .deleteByChatIdAndUserId(UUID.fromString(content.getChatId()), userId)
                                                .then(messageRepository.save(Message.builder()
                                                        .eventType(ChatEventType.PARTICIPANT_LEFT)
                                                        .sentDate(Instant.now())
                                                        .messageContent(ParticipantLeftContent.builder()
                                                                .participantId(actionArguments.senderId.toString())
                                                                .chatId(message.getContent().getChatId())
                                                                .build()
                                                        ).build()
                                                ))
                                );
                    }
            );
            storeActions.put(ChatEventType.PARTICIPANT_REMOVED,
                    (actionArguments) -> {
                        final ParticipantRemovedDTO message = (ParticipantRemovedDTO) actionArguments.messageDTO;
                        final var content = (ParticipantRemovedContentDTO) message.getContent();

                        return chatUserRepository
                                .deleteByChatIdAndUserId(
                                        UUID.fromString(content.getChatId()),
                                        UUID.fromString(content.getRemovedUserId())
                                ).then(messageRepository.save(Message.builder()
                                        .eventType(ChatEventType.PARTICIPANT_REMOVED)
                                        .sentDate(Instant.now())
                                        .messageContent(ParticipantRemovedContent.builder()
                                                .chatId(content.getChatId())
                                                .removerId(actionArguments.senderId.toString())
                                                .removedParticipantId(content.getRemovedUserId())
                                                .build()
                                        ).build()
                                ));
                    }
            );
        }
    }
}
