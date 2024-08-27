package com.mobi.ripple_be.util;

import lombok.Getter;

@Getter
public enum Role {
    ANONYMOUS("ROLE_ANONYMOUS"),
    USER("ROLE_USER"),
    MODERATOR("ROLE_MODERATOR"),
    ADMIN("ROLE_ADMIN");
    Role(String roleName) {
        this.roleName = roleName;
    }
    private final String roleName;
}
