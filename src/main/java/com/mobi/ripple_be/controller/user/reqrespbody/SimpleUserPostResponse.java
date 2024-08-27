package com.mobi.ripple_be.controller.user.reqrespbody;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserPostResponse {
    private String id;
    private byte[] image;
    private String authorId;
}
