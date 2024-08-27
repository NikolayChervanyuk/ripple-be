package com.mobi.ripple_be.chat.websocket.dto.contentdto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantLeftContentDTO implements MessageContentDTO{
    @NotNull
    private String chatId;
}
