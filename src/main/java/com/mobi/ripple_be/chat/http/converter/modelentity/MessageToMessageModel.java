package com.mobi.ripple_be.chat.http.converter.modelentity;

import com.mobi.ripple_be.chat.entity.mongo.Message;
import com.mobi.ripple_be.chat.http.model.MessageModel;
import com.mobi.ripple_be.converter.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class MessageToMessageModel extends BaseConverter<Message, MessageModel> {
    @Override
    public MessageModel convert(Message source) {
        return MessageModel.builder()
                .eventType(source.getEventType())
                .sentDate(source.getSentDate())
                .messageContent(source.getMessageContent())
                .build();
    }
}
