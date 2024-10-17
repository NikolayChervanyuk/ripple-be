package com.mobi.ripple_be.chat.websocket.dto.contentdto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageContentDTO implements MessageContentDTO {
    @NotNull
    private String senderId;
    @NotNull
    private String chatId;
    private String message;
    private String fileName;
    private String fileExtension;
}
