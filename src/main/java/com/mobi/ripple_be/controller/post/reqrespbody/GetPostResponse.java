package com.mobi.ripple_be.controller.post.reqrespbody;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetPostResponse {

    private UUID id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    private UUID authorId;

    private byte[] postImage;

    private String caption;

    private Long likesCount;

    private boolean liked;

    private Long commentsCount;
}
