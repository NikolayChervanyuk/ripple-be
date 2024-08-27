package com.mobi.ripple_be.chat.websocket.dto.content;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantLeftContent implements MessageContent {
    private String participantId;
    @NotNull
    private String chatId;
}
