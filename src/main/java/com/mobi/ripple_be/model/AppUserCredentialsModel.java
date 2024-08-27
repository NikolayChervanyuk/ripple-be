package com.mobi.ripple_be.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppUserCredentialsModel implements UserDetails {

    private String email = null;

    private String username = null;

    private String password = null;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
