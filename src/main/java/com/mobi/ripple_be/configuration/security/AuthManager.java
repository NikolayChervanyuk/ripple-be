package com.mobi.ripple_be.configuration.security;

import com.mobi.ripple_be.entity.AppUser;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono
                .justOrEmpty(authentication)
                .cast(BearerToken.class)
                .flatMap(auth -> {
                    String username = jwtService.extractUsername(auth.getCredentials());
                    Mono<AppUser> foundUser = userRepository
                            .getByUsername(username)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found in auth manager")));

                    return foundUser
                            .flatMap(user -> {
                                if (jwtService.isTokenValid(auth.getCredentials(), user)) {
                                    return Mono.just(
                                            new UsernamePasswordAuthenticationToken(
                                                    new AuthPrincipal(
                                                            user.getId().toString(),
                                                            user.getUsername(),
                                                            user.getPassword()
                                                    ),
                                                    user.getPassword(),
                                                    user.getAuthorities()
                                            )
                                    );
                                }
                                return Mono.error(new IllegalArgumentException("Invalid token"));
                            });
                });
    }
}
