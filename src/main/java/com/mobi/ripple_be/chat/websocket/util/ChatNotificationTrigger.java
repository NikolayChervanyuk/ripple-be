package com.mobi.ripple_be.chat.websocket.util;

import com.mobi.ripple_be.chat.entity.mongo.Message;
import com.mobi.ripple_be.chat.exception.UnhandledTriggerException;
import com.mobi.ripple_be.chat.repository.mongo.MessageRepository;
import com.mobi.ripple_be.chat.websocket.dto.content.ChatCreatedContent;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@AllArgsConstructor
@Component
public class ChatNotificationTrigger {

    private final ChatMessageResolver messageResolver;
    private final ChatMessageManagerV2 chatMessageManager;
    private final MessageRepository messageRepository;

    public Mono<Void> triggerNotification(ChatEventType eventType, String chatId) {
        return switch (eventType) {
            case CHAT_CREATED -> AuthPrincipalProvider.getAuthenticatedUserIdMono()
                    .flatMap(userId -> {
                        var chatCreatedContent = ChatCreatedContent.builder()
                                .creatorId(userId)
                                .chatId(chatId)
                                .build();
                        return messageRepository.save(Message.builder()
                                .eventType(ChatEventType.CHAT_CREATED)
                                .sentDate(Instant.now())
                                .messageContent(chatCreatedContent)
                                .build()
                        ).flatMap(message -> messageResolver
                                .sendToChatRecipients(message, eventType, chatMessageManager.getSessions())
                        ).then();
                    });
            default -> throw new UnhandledTriggerException("Unhandled notification for event type: " + eventType);
        };
    }
}
