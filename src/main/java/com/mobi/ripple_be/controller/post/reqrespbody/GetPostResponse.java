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
    private String authorFullName;
    private String authorUsername;
    private byte[] authorSmallProfilePicture;
    private boolean isAuthorActive;
    private byte[] postImage;
    private String caption;
    private Long likesCount;
    private boolean liked;
    private Long commentsCount;
}
