package com.mobi.ripple_be.chat.websocket.util;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public interface ChatManager {

    Mono<Void> registerUserSession(WebSocketSession session);
}
