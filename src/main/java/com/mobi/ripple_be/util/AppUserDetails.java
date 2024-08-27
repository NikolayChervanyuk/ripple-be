package com.mobi.ripple_be.util;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

public interface AppUserDetails extends UserDetails {
    Instant getLastIssuedTokenRevocation();
}
