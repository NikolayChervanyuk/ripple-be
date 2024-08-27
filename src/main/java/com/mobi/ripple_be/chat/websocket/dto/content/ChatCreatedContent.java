package com.mobi.ripple_be.chat.websocket.dto.content;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreatedContent implements MessageContent {
    @NotNull
    private String chatId;

    @NotNull
    private String creatorId;
}
