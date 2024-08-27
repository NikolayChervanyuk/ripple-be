package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.CommentLike;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CommentLikeRepository extends R2dbcRepository<CommentLike, UUID> {

    Mono<CommentLike> findByAuthorIdAndParentCommentId(UUID authorId, UUID parentCommentId);

    @Modifying
    Mono<Boolean> deleteByAuthorIdAndParentCommentId(UUID authorId, UUID parentCommentId);

    @Modifying
    Mono<Boolean> deleteByParentCommentId(UUID parentCommentId);
}
