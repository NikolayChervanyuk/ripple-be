package com.mobi.ripple_be.chat.http.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.chat.entity.postgres.Chat;
import com.mobi.ripple_be.chat.http.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class ChatToChatModel extends BaseConverter<Chat, ChatModel> {
    @Override
    public ChatModel convert(Chat source) {
        return ChatModel.builder()
                .id(source.getId().toString())
                .name(source.getChatName())
                .createdDate(source.getCreationDate())
                .lastUpdatedDate(source.getLastModifiedDate())
                .build();
    }
}
