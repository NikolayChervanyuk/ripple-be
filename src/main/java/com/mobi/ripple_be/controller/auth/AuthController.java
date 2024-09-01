package com.mobi.ripple_be.controller.auth;


import com.mobi.ripple_be.controller.auth.reqrespbody.*;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.model.BearerTokenModel;
import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import com.mobi.ripple_be.service.AuthService;
import com.mobi.ripple_be.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@RestController
@ControllerAdvice
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ConversionService conversionService;
    private final AuthService authService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<RespModelImpl<LoginResponse>>> authenticate(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return userService
                .getUserCredentialsByIdentifier(loginRequest.getIdentifier(), loginRequest.getPassword()).map(
                        user -> ResponseEntity.ok(
                                RespModelImpl.of(conversionService.convert(user, LoginResponse.class))
                        )
                ).defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("Invalid credentials"), HttpStatus.NOT_FOUND
                ));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        var userModel = conversionService.convert(registerRequest, AppUserModel.class);

        assert userModel != null;
        return userService.saveUser(userModel).mapNotNull(persistenceStatus ->
                switch (persistenceStatus) {
                    case SUCCESS -> ResponseEntity.ok(RespModelImpl.of(true));
                    case USERNAME_EXISTS, EMAIL_EXISTS, INTERNAL_ERROR -> new ResponseEntity<>(
                            RespModelImpl.ofError(persistenceStatus.getStatusMessage()),
                            HttpStatus.CONFLICT
                    );
                }
        );
    }

    @PostMapping("/refresh-token")
    public Mono<ResponseEntity<RespModelImpl<RefreshTokenResponse>>> refreshTokens(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        return authService.refreshTokens(
                        Objects.requireNonNull(conversionService.convert(refreshTokenRequest, BearerTokenModel.class))
                ).mapNotNull(bearerTokenModel -> conversionService.convert(bearerTokenModel, RefreshTokenResponse.class))
                .map(refreshTokenResponse -> ResponseEntity.ok(RespModelImpl.of(refreshTokenResponse)))
                .onErrorReturn(new ResponseEntity<>(
                                RespModelImpl.ofError("Invalid refresh token"),
                                HttpStatus.UNAUTHORIZED
                        )
                );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> logout() {
        return userService.issueTokenRevocation()
                .map(user -> ResponseEntity.ok(RespModelImpl.of(true)))
                .onErrorReturn(new ResponseEntity<>(
                                RespModelImpl.serviceUnavailableError(),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );
    }
}
