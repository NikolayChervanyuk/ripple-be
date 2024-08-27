package com.mobi.ripple_be.chat.websocket.dto.message;

import com.mobi.ripple_be.chat.websocket.dto.GenericMessageDTO;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.ChatCreatedContentDTO;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class ChatCreatedDTO extends GenericMessageDTO<ChatCreatedContentDTO> {
}
