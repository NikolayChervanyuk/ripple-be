package com.mobi.ripple_be.chat.websocket.dto.content;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageContent implements MessageContent {
    @NotNull
    private String senderId;
    @NotNull
    private String chatId;
    private String message;
    private String fileName;
    private String fileExtension;
}

