package com.mobi.ripple_be.chat.http.model;

import com.mobi.ripple_be.chat.websocket.dto.content.MessageContent;
import com.mobi.ripple_be.chat.websocket.util.ChatEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Builder
@Getter
@Setter
public class MessageModel {
    @NotNull
    private ChatEventType eventType;
    @NotNull
    private Instant sentDate;

    private MessageContent messageContent;
}
