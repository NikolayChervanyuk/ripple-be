package com.mobi.ripple_be.chat.websocket.dto.contentdto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatOpenedContentDTO implements MessageContentDTO {
    private String userId;
    @NotNull
    private String chatId;
}
