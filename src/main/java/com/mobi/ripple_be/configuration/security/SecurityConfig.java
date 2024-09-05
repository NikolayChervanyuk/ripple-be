package com.mobi.ripple_be.configuration.security;

import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.util.SerializationUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            AuthConverter jwtAuthConverter,
            AuthManager jwtAuthManager
    ) {

        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthManager);
//        jwtFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        jwtFilter.setServerAuthenticationConverter(jwtAuthConverter);
        return http
                .authorizeExchange(auth -> {
                    auth.pathMatchers("auth/login", "auth/register").permitAll();
                    auth.pathMatchers("users").permitAll();
                    auth.anyExchange().authenticated();
                })
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(spec -> spec.disable())
                .formLogin(spec -> spec.disable())
                .csrf(spec -> spec.disable())
                .build();
    }

    public ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return (webFilterExchange, exception) -> {
            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.NOT_FOUND);

            return webFilterExchange.getExchange().getResponse().writeWith(
                    Mono.fromSupplier(() -> new DefaultDataBufferFactory()
                            .wrap(Objects.requireNonNull(
                                    SerializationUtils.serialize(RespModelImpl.ofError(exception.getMessage()))
                            ))
                    )
            );
        };
    }
}
