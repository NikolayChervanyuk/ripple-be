package com.mobi.ripple_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostCommentModel {

    private String id;
    private String parentPostId;
    private String authorId;
    private String authorName;
    private String authorUsername;
    private Instant creationDate;
    private Instant lastModifiedDate;
    private Long likesCount;
    private boolean isLikedByUser;
    private Long repliesCount;
    private String comment;
}
