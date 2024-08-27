package com.mobi.ripple_be.chat.websocket.dto.contentdto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOnlineContentDTO implements MessageContentDTO{
    private String userId;
}
