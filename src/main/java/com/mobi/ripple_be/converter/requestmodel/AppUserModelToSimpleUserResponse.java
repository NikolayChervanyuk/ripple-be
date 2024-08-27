package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.user.reqrespbody.SimpleUserResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.AppUserModel;
import org.springframework.stereotype.Component;

@Component
public class AppUserModelToSimpleUserResponse extends BaseConverter<AppUserModel, SimpleUserResponse> {
    @Override
    public SimpleUserResponse convert(AppUserModel source) {
        return SimpleUserResponse.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .username(source.getUsername())
                .isActive(source.isActive())
                .build();
    }
}
