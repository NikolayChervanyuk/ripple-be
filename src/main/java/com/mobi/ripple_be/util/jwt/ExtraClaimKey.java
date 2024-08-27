package com.mobi.ripple_be.util.jwt;

import lombok.Getter;

@Getter
public enum ExtraClaimKey {
    TOKEN_TYPE("typ");

    ExtraClaimKey(String claimName) {
        this.claimName = claimName;
    }
    private final String claimName;
}
