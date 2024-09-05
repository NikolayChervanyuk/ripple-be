package com.mobi.ripple_be.controller.user.reqrespbody;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserResponse {

    private String id;
    private String fullName;
    private String username;
    private boolean isActive;
    private byte[] smallProfilePicture;
}
