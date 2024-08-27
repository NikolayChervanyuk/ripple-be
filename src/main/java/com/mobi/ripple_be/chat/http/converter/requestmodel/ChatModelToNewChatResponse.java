package com.mobi.ripple_be.chat.http.converter.requestmodel;

import com.mobi.ripple_be.chat.http.controller.reqresp.NewChatResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.chat.http.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class ChatModelToNewChatResponse extends BaseConverter<ChatModel, NewChatResponse> {
    @Override
    public NewChatResponse convert(ChatModel source) {
        return NewChatResponse.builder()
                .chatId(source.getId())
                .build();
    }
}
