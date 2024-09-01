package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.user.reqrespbody.UpdateUserRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.AppUserModel;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserRequestToAppUserModel extends BaseConverter<UpdateUserRequest, AppUserModel> {
    @Override
    public AppUserModel convert(UpdateUserRequest source) {
        return AppUserModel.builder()
                .fullName(source.getFullName())
                .username(source.getUsername())
                .email(source.getEmail())
                .bio(source.getBio())
                .build();
    }
}
