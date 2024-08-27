package com.mobi.ripple_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUserModel {

    private String id;

    private String fullName;

    private String email;

    private String username;

    private String bio;

    private String password;

    private Long followers;

    private Long following;

    private boolean isFollowed;

    private boolean isActive;

    private Instant lastActive;

    private Long postsCount;

    private Instant lastIssuedTokenRevocation;
}
