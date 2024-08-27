package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.PostComment;
import com.mobi.ripple_be.view.PostCommentView;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PostCommentRepository extends R2dbcRepository<PostComment, UUID> {

    @Query("SELECT pc.id AS id, " +
            "p.id AS parent_post_id, " +
            "u.id AS author_id, " +
            "u.full_name AS author_name, " +
            "u.username AS author_username, " +
            "pc.creation_date AS creation_date, " +
            "pc.last_modified_date AS last_modified_date, " +
            "pc.likes_count AS likes_count, " +
            "pc.replies_count AS replies_count, " +
            "pc.comment AS comment " +
            "FROM post AS p " +
            "JOIN post_comment AS pc ON pc.parent_post_id = :postId " +
            "JOIN app_user AS u ON u.id = pc.author_id " +
            "WHERE p.id = :postId " +
            "ORDER BY pc.likes_count DESC " +
            "LIMIT 1")
    Mono<PostCommentView> getTopPostComment(UUID postId);

    @Query("SELECT pc.id AS id, " +
            "p.id AS parent_post_id, " +
            "u.id AS author_id, " +
            "u.full_name AS author_name, " +
            "u.username AS author_username, " +
            "pc.creation_date AS creation_date, " +
            "pc.last_modified_date AS last_modified_date, " +
            "pc.likes_count AS likes_count, " +
            "pc.replies_count AS replies_count, " +
            "pc.comment AS comment " +
            "FROM post AS p " +
            "JOIN post_comment AS pc ON pc.parent_post_id = :postId " +
            "JOIN app_user AS u ON u.id = pc.author_id " +
            "WHERE p.id = :postId " +
            "ORDER BY pc.likes_count DESC " +
            "OFFSET :offset " +
            "LIMIT :size")
    Flux<PostCommentView> getTopPostComments(UUID postId, int offset, int size);

    Mono<PostComment> getPostCommentByParentPostIdAndId(UUID parentPostId, UUID id);

    @Modifying
    @Query("WITH PostCommentDeletes AS (" +
            "DELETE FROM post_comment AS pc " +
            "WHERE pc.id = :id RETURNING pc.id" +
            "), " +
            "CommentLikeDeletes AS (" +
            "DELETE FROM comment_like AS cl " +
            "WHERE cl.parent_comment_id = PostCommentDeletes.id RETURNING PostCommentDeletes.id" +
            "), " +
            "CommentReplyDeletes AS (" +
            "DELETE FROM comment_reply AS cr " +
            "WHERE cr.parent_comment_id = PostCommentDeletes.id RETURNING cr.id" +
            ") " +
            "DELETE FROM reply_like AS rl WHERE rl.parent_reply_id = CommentReplyDeletes.id"
    )
    Mono<Boolean> deletePostCommentByIdCascade(UUID id);
}
