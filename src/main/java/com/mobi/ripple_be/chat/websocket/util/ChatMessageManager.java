//package com.mobi.ripple_be.chat.websocket.util;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.mobi.ripple_be.chat.entity.mongo.Message;
//import com.mobi.ripple_be.chat.entity.mongo.PendingMessageUser;
//import com.mobi.ripple_be.chat.repository.mongo.MessageRepository;
//import com.mobi.ripple_be.chat.repository.mongo.PendingMessageUserRepository;
//import com.mobi.ripple_be.chat.websocket.dto.GenericMessageDTO;
//import com.mobi.ripple_be.chat.websocket.dto.content.ChatCreatedContent;
//import com.mobi.ripple_be.chat.websocket.dto.content.ChatOpenedContent;
//import com.mobi.ripple_be.chat.websocket.dto.content.NewMessageContent;
//import com.mobi.ripple_be.chat.websocket.dto.contentdto.NewMessageContentDTO;
//import com.mobi.ripple_be.chat.websocket.dto.message.ChatCreatedDTO;
//import com.mobi.ripple_be.chat.websocket.dto.message.ChatOpenedDTO;
//import com.mobi.ripple_be.chat.websocket.dto.message.NewMessageDTO;
//import com.mobi.ripple_be.chat.websocket.dto.message.UserOnlineDTO;
//import com.mobi.ripple_be.exception.ApplicationException;
//import com.mobi.ripple_be.repository.UserFollowingRepository;
//import com.mobi.ripple_be.repository.UserRepository;
//import com.mobi.ripple_be.service.MediaService;
//import com.mobi.ripple_be.util.AuthPrincipalProvider;
//import jakarta.validation.Valid;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.DefaultDataBufferFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.socket.WebSocketMessage;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.UUID;
//
//@Slf4j
//@Component
//public class ChatMessageManager implements ChatManager {
//
//    private final ChatObjectMapper chatObjectMapper;
//    private final MediaService mediaService;
//    private final UserRepository userRepository;
//    private final MessageRepository messageRepository;
//    private final PendingMessageUserRepository pendingMessageUserRepository;
//    private final UserFollowingRepository userFollowingRepository;
//
//    private final HashMap<UUID, WebSocketSession> sessionHashMap = new HashMap<>();
//    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
//
//    @Autowired
//    public ChatMessageManager(
//            ChatObjectMapper chatObjectMapper,
//            MediaService mediaService,
//            UserRepository userRepository,
//            MessageRepository messageRepository,
//            PendingMessageUserRepository pendingMessageUserRepository,
//            UserFollowingRepository userFollowingRepository) {
//        this.chatObjectMapper = chatObjectMapper;
//        this.mediaService = mediaService;
//        this.userRepository = userRepository;
//        this.messageRepository = messageRepository;
//        this.pendingMessageUserRepository = pendingMessageUserRepository;
//        this.userFollowingRepository = userFollowingRepository;
//    }
//
//    public Mono<Void> registerUserSession(WebSocketSession session) {
//
//        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
//                .flatMap(userId -> sessionInitialization(session, userId)
//                        .then(session.send(getAnyPendingMessages(session, userId)))
//                        .thenMany(
//                                session.receive().doOnNext(WebSocketMessage::retain)
//                                        .flatMap(message ->
//                                                storeIncomingMessage(getDeserializedMessage(message), userId)
//                                        ).flatMap(messageVariant -> processIncomingMessage(messageVariant, userId))
//                        ).thenEmpty(sessionFinalization(userId))
//                ).doOnError(Throwable::printStackTrace);
//    }
//
//    private Mono<Void> sessionInitialization(WebSocketSession session, UUID userId) {
//        return userRepository.findById(userId)
//                .flatMap(user -> {
//                    user.setActive(true);
//                    return userRepository.save(user).then();
//                })
//                .doFinally(sig -> sessionHashMap.put(userId, session))
//                .then();
//    }
//
//    private Flux<WebSocketMessage> getAnyPendingMessages(WebSocketSession session, UUID userId) {
//        return pendingMessageUserRepository.findByUserId(userId.toString())
//                .flatMap(pendingMessageUser -> messageRepository
//                        .findById(pendingMessageUser.getMsgId())
//                        .publishOn(Schedulers.boundedElastic())
//                        .handle((message, sink) -> {
//                            String messageJson;
//                            try {
//                                messageJson = chatObjectMapper.writeValueAsString(message);
//                            } catch (JsonProcessingException e) {
//                                sink.error(new RuntimeException(e));
//                                return;
//                            }
//                            pendingMessageUserRepository.delete(pendingMessageUser).subscribe();
//                            sink.next(session.textMessage(messageJson));
//                        })
//                        .cast(WebSocketMessage.class)
//                );
//    }
//
//    private Flux<Void> processIncomingMessage(Message storedMessage, UUID userId) {
//        return switch (storedMessage.getEventType()) {
//            case NEW_MESSAGE, CHAT_OPENED, CHAT_CREATED -> userRepository.findAppUserViewsFromChat(
//                            switch (storedMessage.getEventType()) {
//                                case NEW_MESSAGE -> UUID.fromString(
//                                        ((NewMessageContent) storedMessage.getMessageContent()).getChatId()
//                                );
//                                case CHAT_OPENED -> UUID.fromString(
//                                        ((ChatOpenedContent) storedMessage.getMessageContent()).getChatId()
//                                );
//                                case CHAT_CREATED -> UUID.fromString(
//                                        ((ChatCreatedContent) storedMessage.getMessageContent()).getChatId()
//                                );
//                                default -> UUID.fromString("Code should never reach here!");
//                            }
//                    )
//                    .publishOn(Schedulers.boundedElastic())
//                    .flatMap(userView -> {
//                        var foundUserSession = sessionHashMap.get(userView.getId());
//                        if (foundUserSession == null) {
//                            return pendingMessageUserRepository.save(PendingMessageUser.builder()
//                                    .userId(userView.getId().toString())
//                                    .msgId(storedMessage.getMsgId())
//                                    .build()
//                            ).then();
//                        } else {
//                            if (!userView.getId().equals(userId)) {
//                                try {
//                                    return foundUserSession.send(Mono.just(
//                                            new WebSocketMessage(
//                                                    WebSocketMessage.Type.TEXT,
//                                                    getBufferWithJsonMessage(storedMessage)
//                                            )
//                                    ));
//                                } catch (JsonProcessingException e) {
//                                    return Flux.error(new RuntimeException(e));
//                                }
//                            }
//                            return Mono.empty();
//                        }
//                    });
//            case USER_ONLINE -> userFollowingRepository.findByFollowingId(userId)
//                    .publishOn(Schedulers.boundedElastic())
//                    .flatMap(userFollowing -> {
//                        var followerId = userFollowing.getUserId();
//                        var foundUserSession = sessionHashMap.get(followerId);
//                        if (foundUserSession == null) {
//                            return pendingMessageUserRepository.save(PendingMessageUser.builder()
//                                    .userId(followerId.toString())
//                                    .msgId(storedMessage.getMsgId())
//                                    .build()
//                            ).then();
//                        } else {
//                            try {
//                                return foundUserSession.send(Mono.just(
//                                        new WebSocketMessage(
//                                                WebSocketMessage.Type.TEXT,
//                                                getBufferWithJsonMessage(storedMessage)
//                                        )
//                                ));
//                            } catch (JsonProcessingException e) {
//                                return Flux.error(new RuntimeException(e));
//                            }
//                        }
//                    });
//        };
//    }
//
//    private Mono<Void> sessionFinalization(UUID userId) {
//        return userRepository.findById(userId)
//                .flatMap(user -> {
//                    sessionHashMap.remove(userId);
//                    user.setActive(false);
//                    user.setLastActive(Instant.now());
//                    return userRepository.save(user).then();
//                }).then();
//    }
//
//    @SneakyThrows
//    private DataBuffer getBufferWithJsonMessage(Message messageToSerialize) throws JsonProcessingException {
//        //TODO: Make more readable
//        //TODO: when client sends new massage, it should create the file via http POST request,
//        // then in new message should be stored the name of the file, so when connected users
//        // can retrieve it via http GET. That is because max frame length of message via websocket is 65KB
//        final byte[] jsonValBytes;
//        jsonValBytes = chatObjectMapper.writeValueAsString(messageToSerialize).getBytes();
//        DataBuffer buffer = bufferFactory.allocateBuffer(jsonValBytes.length);
//        return buffer.write(jsonValBytes);
//    }
//
//    @SuppressWarnings({"rawtypes"})
//    private Mono<Message> storeIncomingMessage(GenericMessageDTO receivedMsg, UUID userId) {
//        if (receivedMsg instanceof UserOnlineDTO) {
//            return messageRepository.save(Message.builder()
//                    .eventType(ChatEventType.USER_ONLINE)
//                    .sentDate(Instant.now())
//                    .messageContent(null)
//                    .build()
//            );
//        } else if (receivedMsg instanceof @Valid NewMessageDTO message) {
//            NewMessageContentDTO content = message.getContent();
////            String msgFilePath = null;
////            if (content.getFileName() != null && !content.getFileName().isBlank()) {
////                msgFilePath =
////                        mediaService.storeChatFile(
////                        content.getChatId(),
////                        content.getFileName(),
////                        content.getFileExtension()
////                ).toString();
////            }
//            return messageRepository.save(Message.builder()
//                    .eventType(ChatEventType.NEW_MESSAGE)
//                    .sentDate(Instant.now())
//                    .messageContent(NewMessageContent.builder()
//                            .senderId(userId.toString())
//                            .chatId(content.getChatId())
//                            .message(content.getMessage())
//                            .fileName(content.getFileName())
//                            .fileExtension(content.getFileName() != null ? content.getFileExtension() : null)
//                            .build()
//                    ).build()
//            );
//        } else if (receivedMsg instanceof @Valid ChatOpenedDTO message) {
//            return messageRepository.save(Message.builder()
//                    .eventType(ChatEventType.CHAT_OPENED)
//                    .sentDate(Instant.now())
//                    .messageContent(
//                            ChatOpenedContent.builder()
//                                    .userId(userId.toString())
//                                    .chatId(message.getContent().getChatId())
//                                    .build()
//                    ).build()
//            );
//        } else if (receivedMsg instanceof @Valid ChatCreatedDTO message) {
//            return messageRepository.save(Message.builder()
//                    .eventType(ChatEventType.CHAT_CREATED)
//                    .sentDate(Instant.now())
//                    .messageContent(
//                            ChatCreatedContent.builder()
//                                    .creatorId(userId.toString())
//                                    .chatId(message.getContent().getChatId())
//                                    .build()
//                    ).build()
//            );
//        }
//        throw new ApplicationException("eventType not recognized");
//    }
//
//    @SuppressWarnings("rawtypes")
//    private GenericMessageDTO getDeserializedMessage(WebSocketMessage webSocketMessage) {
//        try (InputStream chatMessageStream = webSocketMessage.getPayload().asInputStream()) {
//            JsonNode jsonNode = chatObjectMapper.readTree(chatMessageStream.readAllBytes());
//            String eventType = jsonNode.get("eventType").textValue();
//            if (eventType == null) {
//                throw new ApplicationException("eventType can't be null");
//            }
//            if (eventType.equals(ChatEventType.USER_ONLINE.getLiteral())) {
//                return chatObjectMapper.convertValue(jsonNode, UserOnlineDTO.class);
//            } else if (eventType.equals(ChatEventType.NEW_MESSAGE.getLiteral())) {
//                return chatObjectMapper.convertValue(jsonNode, NewMessageDTO.class);
//            } else if (eventType.equals(ChatEventType.CHAT_OPENED.getLiteral())) {
//                return chatObjectMapper.convertValue(jsonNode, ChatOpenedDTO.class);
//            } else if (eventType.equals(ChatEventType.CHAT_CREATED.getLiteral())) {
//                return chatObjectMapper.convertValue(jsonNode, ChatCreatedDTO.class);
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }
//}
