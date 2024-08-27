package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.PostLike;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PostLikeRepository extends R2dbcRepository<PostLike, UUID> {

    Mono<PostLike> findByParentPostIdAndAuthorId(UUID postId, UUID authorId);

    @Modifying
    Mono<Boolean> deleteByAuthorIdAndParentPostId(UUID authorId, UUID parentPostId);
}
