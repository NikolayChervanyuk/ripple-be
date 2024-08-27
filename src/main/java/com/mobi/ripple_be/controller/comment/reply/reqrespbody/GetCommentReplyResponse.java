package com.mobi.ripple_be.controller.comment.reply.reqrespbody;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class GetCommentReplyResponse {

    private String id;
    private String authorName;
    private String authorUsername;
    private Instant createdDate;
    private Instant lastUpdatedDate;
    private Long likesCount;
    private String reply;
}
