package com.mobi.ripple_be.chat.websocket.dto.content;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewParticipantContent implements MessageContent {
    @NotNull
    private String chatId;

    private String inviterId;
    @NotNull
    private String participantId;

}
