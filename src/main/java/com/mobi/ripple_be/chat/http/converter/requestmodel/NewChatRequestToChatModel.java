package com.mobi.ripple_be.chat.http.converter.requestmodel;

import com.mobi.ripple_be.chat.http.controller.reqresp.NewChatRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.chat.http.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class NewChatRequestToChatModel extends BaseConverter<NewChatRequest, ChatModel> {

    @Override
    public ChatModel convert(NewChatRequest source) {
        return ChatModel.builder()
                .name(source.getName())
                .participantIds(source.getUserToAddIds())
                .build();
    }
}
