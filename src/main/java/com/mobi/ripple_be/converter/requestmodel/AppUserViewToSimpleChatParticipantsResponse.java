package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.chat.http.controller.reqresp.SimpleChatParticipantsResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.view.AppUserView;
import org.springframework.stereotype.Component;

@Component
public class AppUserViewToSimpleChatParticipantsResponse extends BaseConverter<AppUserView, SimpleChatParticipantsResponse> {

    @Override
    public SimpleChatParticipantsResponse convert(AppUserView source) {
        return SimpleChatParticipantsResponse.builder()
                .participantId(source.getId().toString())
                .name(source.getFullName())
                .username(source.getUsername())
                .isActive(source.getIsActive())
                .lastActive(source.getLastActive())
                .build();
    }
}
