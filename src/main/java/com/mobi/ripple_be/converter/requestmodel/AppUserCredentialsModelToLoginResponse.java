package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.auth.reqrespbody.LoginResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.AppUserCredentialsModel;
import com.mobi.ripple_be.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppUserCredentialsModelToLoginResponse extends BaseConverter<AppUserCredentialsModel, LoginResponse> {

    private final JwtService jwtService;

    @Override
    public LoginResponse convert(@NonNull AppUserCredentialsModel source) {
        return LoginResponse.builder()
                .refreshToken(jwtService.generateRefreshToken(source))
                .accessToken(jwtService.generateAccessToken(source))
                .build();
    }
}
