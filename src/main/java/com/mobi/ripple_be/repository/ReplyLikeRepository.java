package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.ReplyLike;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ReplyLikeRepository extends R2dbcRepository<ReplyLike, UUID> {

    Mono<ReplyLike> findByAuthorIdAndParentReplyId(UUID authorId, UUID parentReplyId);

    @Modifying
    Mono<Boolean> deleteByAuthorIdAndParentReplyId(UUID authorId, UUID parentReplyId);

    @Modifying
    Mono<Boolean> deleteByParentReplyId(UUID parentReplyId);
}
