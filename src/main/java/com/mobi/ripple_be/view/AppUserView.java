package com.mobi.ripple_be.view;

import com.mobi.ripple_be.util.Role;

import java.time.Instant;
import java.util.UUID;

public interface AppUserView {

    UUID getId();

    String getEmail();

    String getUsername();

    String getFullName();

    String getBio();

    Long getFollowers();

    Long getFollowing();

    boolean getIsActive();

    Instant getLastActive();

    Long getPostsCount();

    Role getRole();

    Instant getLastIssuedTokenRevocation();
}
