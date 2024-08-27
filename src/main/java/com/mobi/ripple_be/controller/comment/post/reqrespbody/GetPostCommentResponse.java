package com.mobi.ripple_be.controller.comment.post.reqrespbody;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class GetPostCommentResponse {

    private String commentId;
    private String authorName;
    private String authorUsername;
    private Instant createdDate;
    private Instant lastUpdatedDate;
    private Long likesCount;
    private boolean liked;
    private Long repliesCount;
    private String comment;
}
