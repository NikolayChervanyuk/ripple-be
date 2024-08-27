package com.mobi.ripple_be.controller.user.reqrespbody;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String id;
    private String fullName;
    private String username;
    private String bio;
    private Long followers;
    private Long following;
    private boolean isFollowed;
    private boolean isActive;
    private Instant lastActive;
    private Long postsCount;
}
