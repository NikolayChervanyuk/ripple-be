package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.CommentReply;
import com.mobi.ripple_be.view.CommentReplyView;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CommentReplyRepository extends R2dbcRepository<CommentReply, UUID> {

    @Query("SELECT cr.id AS id, " +
            "cr.parent_comment_id AS parent_comment_id, " +
            "u.id AS author_id, " +
            "u.full_name AS author_name, " +
            "u.username AS author_username," +
            "cr.creation_date AS created_date, " +
            "cr.last_modified_date AS last_updated_date," +
            "cr.likes_count AS likes_count, " +
            "cr.comment AS reply " +
            "FROM comment_reply AS cr " +
            "JOIN app_user AS u ON u.id = cr.author_id " +
            "WHERE cr.parent_comment_id = :parentCommentId " +
            "ORDER BY cr.creation_date DESC " +
            "OFFSET :offset " +
            "LIMIT :size")
    Flux<CommentReplyView> getLatestCommentReplies(UUID parentCommentId, int offset, int size);

    Flux<CommentReply> findByParentCommentId(UUID parentCommentId);

    @Modifying
    Mono<Boolean> deleteByParentCommentId(UUID parentCommentId);

}
