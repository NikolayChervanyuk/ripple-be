package com.mobi.ripple_be.chat.websocket.dto.contentdto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantRemovedContentDTO implements MessageContentDTO {

    @NotNull
    private String chatId;
    private String removerId;
    @NotNull
    private String removedUserId;
}
