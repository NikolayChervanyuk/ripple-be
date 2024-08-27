package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.auth.reqrespbody.RegisterRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.AppUserModel;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RegisterRequestToAppUserModel extends BaseConverter<RegisterRequest, AppUserModel> {
    @Override
    public AppUserModel convert(RegisterRequest source) {
        return AppUserModel.builder()
                .fullName(source.getFullName())
                .email(source.getEmail())
                .username(source.getUsername())
                .password(source.getPassword())
                .followers(0L)
                .following(0L)
                .postsCount(0L)
                .lastIssuedTokenRevocation(Instant.now())
                .build();
    }
}
