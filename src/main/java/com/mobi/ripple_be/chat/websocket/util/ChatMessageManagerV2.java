package com.mobi.ripple_be.chat.websocket.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mobi.ripple_be.chat.repository.mongo.MessageRepository;
import com.mobi.ripple_be.chat.repository.mongo.PendingMessagesUsersRepository;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class ChatMessageManagerV2 implements ChatManager {

    private final ChatObjectMapper chatObjectMapper;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final PendingMessagesUsersRepository pendingMessagesUsersRepository;

    private final ChatMessageResolver chatMessageResolver;

    @Getter
    private final HashMap<UUID, WebSocketSession> sessions = new HashMap<>();

    @Override
    public Mono<Void> registerUserSession(WebSocketSession session) {
        log.info("New user connected to websocket");
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userId -> sessionInitialization(session, userId)
                        .then(session.send(getAnyPendingMessages(session, userId)))
                        .thenMany(session.receive()
                                .doOnNext(WebSocketMessage::retain)
                                .map(chatMessageResolver::resolve)
                                .flatMap(resolvedMessage -> resolvedMessage
                                        .storeMessage(userId)
                                        .flatMap(storedMessage -> resolvedMessage
                                                .sendToChatRecipients(storedMessage, userId, sessions)
                                        )
                                )
                        ).thenEmpty(sessionFinalization(userId))
                ).doOnError(Throwable::printStackTrace);
    }

    private Mono<Void> sessionInitialization(WebSocketSession session, UUID userId) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    user.setActive(true);
                    return userRepository.save(user).then();
                })
                .doFinally(sig -> sessions.put(userId, session))
                .then();
    }

    private Flux<WebSocketMessage> getAnyPendingMessages(WebSocketSession session, UUID userId) {
        return pendingMessagesUsersRepository.findByUserId(userId.toString())
                .flatMap(pendingMessageUser -> messageRepository
                        .findById(pendingMessageUser.getMsgId())
                        .publishOn(Schedulers.boundedElastic())
                        .handle((message, sink) -> {
                            String messageJson;
                            try {
                                messageJson = chatObjectMapper.writeValueAsString(message);
                            } catch (JsonProcessingException e) {
                                sink.error(new RuntimeException(e));
                                return;
                            }
                            pendingMessagesUsersRepository.delete(pendingMessageUser).subscribe();
                            sink.next(session.textMessage(messageJson));
                        })
                        .cast(WebSocketMessage.class)
                );
    }

    private Mono<Void> sessionFinalization(UUID userId) {
        log.info("User disconnected");
        return userRepository.findById(userId)
                .flatMap(user -> {
                    sessions.remove(userId);
                    user.setActive(false);
                    user.setLastActive(Instant.now());
                    return userRepository.save(user).then();
                }).then();
    }
}
