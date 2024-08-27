package com.mobi.ripple_be.chat.http.controller.reqresp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NewMessageFileResponse {
    String filename;
}
