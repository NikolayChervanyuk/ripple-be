package com.mobi.ripple_be.configuration;


import com.mobi.ripple_be.chat.websocket.util.ChatMessageManagerV2;
import com.mobi.ripple_be.chat.websocket.util.ChatMessageWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class WebSocketConfig {

    @Bean
    public ChatMessageWebSocketHandler chatMessageWebSocketHandler(ChatMessageManagerV2 chatMessageManager) {
        return new ChatMessageWebSocketHandler(chatMessageManager);
    }

    @Bean
    public HandlerMapping handlerMapping(ChatMessageWebSocketHandler chatMessageWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/chat-ws-messages", chatMessageWebSocketHandler);
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
