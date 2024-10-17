package com.mobi.ripple_be.chat.http.converter.requestmodel;

import com.mobi.ripple_be.chat.http.controller.reqresp.GetMessageResponse;
import com.mobi.ripple_be.chat.http.model.MessageModel;
import com.mobi.ripple_be.converter.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class MessageModelToGetMessageResponse extends BaseConverter<MessageModel, GetMessageResponse> {
    @Override
    public GetMessageResponse convert(MessageModel source) {
        return GetMessageResponse.builder()
                .eventType(source.getEventType())
                .sentDate(source.getSentDate())
                .messageContent(source.getMessageContent())
                .build();
    }
}
