package com.mobi.ripple_be.chat.websocket.dto.content;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantRemovedContent implements MessageContent {
    @NotNull
    private String chatId;
    private String removerId;
    @NotNull
    private String removedParticipantId;
}
