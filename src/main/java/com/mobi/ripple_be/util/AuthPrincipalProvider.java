package com.mobi.ripple_be.util;

import com.mobi.ripple_be.configuration.security.AuthPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.UUID;


public class AuthPrincipalProvider {

    public static Mono<AuthPrincipal> getAuthenticatedUserMono() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .cast(AuthPrincipal.class)
                .doOnError(Throwable::printStackTrace);
    }

    public static Mono<String> getAuthenticatedUserIdMono() {
        return getAuthenticatedUserMono().map(AuthPrincipal::getId);
    }

    public static Mono<UUID> getAuthenticatedUserUUIDMono() {
        return getAuthenticatedUserMono().map(AuthPrincipal::getId)
                .map(UUID::fromString);
    }


    public static AuthPrincipal getAuthenticatedUser() {
        return getAuthenticatedUserMono().block();
    }

    public static String getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }

    public static UUID getAuthenticatedUserUUID() {
        return UUID.fromString(getAuthenticatedUserId());
    }
}
