package com.mobi.ripple_be.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BearerTokenModel {
    private String refreshToken;
    private String accessToken;
}
