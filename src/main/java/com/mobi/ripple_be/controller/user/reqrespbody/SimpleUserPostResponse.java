package com.mobi.ripple_be.controller.user.reqrespbody;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserPostResponse {
    private String id;
    private byte[] image;
    private String authorId;
    private Instant creationDate;
}
