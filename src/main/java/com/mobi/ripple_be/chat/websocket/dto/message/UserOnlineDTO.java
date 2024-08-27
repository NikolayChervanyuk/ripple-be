package com.mobi.ripple_be.chat.websocket.dto.message;

import com.mobi.ripple_be.chat.websocket.dto.GenericMessageDTO;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.MessageContentDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserOnlineDTO extends GenericMessageDTO<MessageContentDTO> {

}
