package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.auth.reqrespbody.RefreshTokenRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.BearerTokenModel;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenRequestToBearerTokenModel extends BaseConverter<RefreshTokenRequest, BearerTokenModel> {

    @Override
    public BearerTokenModel convert(RefreshTokenRequest source) {
        return BearerTokenModel.builder()
                .refreshToken(source.getRefreshToken())
                .build();
    }
}
