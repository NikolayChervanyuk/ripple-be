package com.mobi.ripple_be.configuration;

import com.mobi.ripple_be.configuration.security.AuthPrincipal;
import com.mobi.ripple_be.entity.AppUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

@Configuration
@EnableR2dbcAuditing
public class AuditConfig {

    @Bean
    public ReactiveAuditorAware<String> securityAuditorProvider() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .cast(AuthPrincipal.class)
                .map(AuthPrincipal::getUsername);
    }
}
