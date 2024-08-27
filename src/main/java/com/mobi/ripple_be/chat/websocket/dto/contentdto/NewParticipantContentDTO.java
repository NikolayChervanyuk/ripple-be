package com.mobi.ripple_be.chat.websocket.dto.contentdto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewParticipantContentDTO implements MessageContentDTO {
    private String inviterId;
    @NotNull
    private String participantId;
    @NotNull
    private String chatId;
}
