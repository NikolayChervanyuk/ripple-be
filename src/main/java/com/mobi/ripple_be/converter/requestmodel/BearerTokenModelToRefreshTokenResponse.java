package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.auth.reqrespbody.RefreshTokenResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.BearerTokenModel;

public class BearerTokenModelToRefreshTokenResponse extends BaseConverter<BearerTokenModel, RefreshTokenResponse> {
    @Override
    public RefreshTokenResponse convert(BearerTokenModel source) {
        return RefreshTokenResponse.builder()
                .refreshToken(source.getRefreshToken())
                .accessToken(source.getAccessToken())
                .build();
    }
}
