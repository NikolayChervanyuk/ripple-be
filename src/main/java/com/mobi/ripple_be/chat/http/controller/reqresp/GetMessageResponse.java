package com.mobi.ripple_be.chat.http.controller.reqresp;

import com.mobi.ripple_be.chat.websocket.dto.content.MessageContent;
import com.mobi.ripple_be.chat.websocket.util.ChatEventType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetMessageResponse {
    @NotNull
    private ChatEventType eventType;
    @NotNull
    private Instant sentDate;

    private MessageContent messageContent;
}
