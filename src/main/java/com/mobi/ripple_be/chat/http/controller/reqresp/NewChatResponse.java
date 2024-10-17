package com.mobi.ripple_be.chat.http.controller.reqresp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NewChatResponse {
    private String chatId;
    private String chatName;
    private Instant createDate;
    private byte[] chatPicture;

}
