package com.mobi.ripple_be.controller.comment.reply.reqrespbody;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCommentReplyResponse {

    private String id;
    private byte[] authorProfilePicture;
    private String authorName;
    private String authorUsername;
    private Instant createdDate;
    private Instant lastUpdatedDate;
    private Long likesCount;
    private boolean isLiked;
    private String reply;
}
