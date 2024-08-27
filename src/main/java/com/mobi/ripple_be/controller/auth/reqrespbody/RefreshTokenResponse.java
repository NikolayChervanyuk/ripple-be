package com.mobi.ripple_be.controller.auth.reqrespbody;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {

    @NotNull
    private String refreshToken;

    @NotNull
    private String accessToken;
}
