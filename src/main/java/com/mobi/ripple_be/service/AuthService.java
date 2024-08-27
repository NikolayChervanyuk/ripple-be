package com.mobi.ripple_be.service;

import com.mobi.ripple_be.exception.ApplicationException;
import com.mobi.ripple_be.model.BearerTokenModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;

    public Mono<BearerTokenModel> refreshTokens(BearerTokenModel bearerTokenModel) {
        String username = jwtService.extractUsername(bearerTokenModel.getRefreshToken());
        return userService.getAppUserModelByUsername(username)
                .map(userModel -> BearerTokenModel.builder()
                        .refreshToken(bearerTokenModel.getRefreshToken()) //TODO: Could implement rotating refresh token
                        .accessToken(jwtService.generateRefreshToken(userModel))
                        .build()
                ).switchIfEmpty(Mono.error(new ApplicationException("No user found with username " + username)));
    }
}
