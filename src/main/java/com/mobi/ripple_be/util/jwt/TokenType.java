package com.mobi.ripple_be.util.jwt;

import lombok.Getter;

@Getter
public enum TokenType {
    REFRESH_TOKEN("refresh_token"),
    ACCESS_TOKEN("access_token");

    TokenType(final String tokenName) {
        this.tokenName = tokenName;
    }
    private final String tokenName;
}
