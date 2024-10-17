package com.mobi.ripple_be.chat.http.converter.requestmodel;

import com.mobi.ripple_be.chat.http.controller.reqresp.GetChatResponse;
import com.mobi.ripple_be.chat.http.model.ChatModel;
import com.mobi.ripple_be.converter.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class ChatModelToGetChatResponse extends BaseConverter<ChatModel, GetChatResponse> {
    @Override
    public GetChatResponse convert(ChatModel source) {
        return GetChatResponse.builder()
                .chatId(source.getId())
                .chatName(source.getName())
                .lastSentTime(source.getLastUpdatedDate())
                .build();
    }
}
