package com.mobi.ripple_be.chat.websocket.util;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ChatMessageWebSocketHandler implements WebSocketHandler {

    private final ChatMessageManagerV2 chatMessageManager;

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        return chatMessageManager.registerUserSession(session);
    }
}
