package com.mobi.ripple_be.controller.auth.reqrespbody;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotNull
    private String refreshToken;
}
