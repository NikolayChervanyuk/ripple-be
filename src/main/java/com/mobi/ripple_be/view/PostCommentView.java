package com.mobi.ripple_be.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentView {

    @Column("id")
    private String id;

    @Column("parent_post_id")
    private String parentPostId;

    @Column("author_id")
    private String authorId;

    @Column("author_name")
    private String authorName;

    @Column("author_username")
    private String authorUsername;

    @Column("creation_date")
    private Instant creationDate;

    @Column("last_modified_date")
    private Instant lastModifiedDate;

    @Column("likes_count")
    private Long likesCount;

    @Column("liked")
    private boolean liked;

    @Column("replies_count")
    private Long repliesCount;

    @Column("comment")
    private String comment;
}
