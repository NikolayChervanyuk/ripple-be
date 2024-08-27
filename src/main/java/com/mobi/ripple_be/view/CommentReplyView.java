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
public class CommentReplyView {

    @Column("id")
    private String id;

    @Column("parent_comment_id")
    private String parentCommentId;

    @Column("author_id")
    private String authorId;

    @Column("author_name")
    private String authorName;

    @Column("author_username")
    private String authorUsername;

    @Column("created_date")
    private Instant createdDate;

    @Column("last_updated_date")
    private Instant lastUpdatedDate;

    @Column("likes_count")
    private Long likesCount;

    @Column("reply")
    private String reply;
}
