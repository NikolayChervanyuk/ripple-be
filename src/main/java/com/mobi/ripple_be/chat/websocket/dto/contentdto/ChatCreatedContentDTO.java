package com.mobi.ripple_be.chat.websocket.dto.contentdto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreatedContentDTO implements MessageContentDTO {
    private String creatorId;
    @NotNull
    private String chatId;

    private String chatName;
}
