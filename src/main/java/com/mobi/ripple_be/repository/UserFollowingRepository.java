package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.UserFollowing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserFollowingRepository extends R2dbcRepository<UserFollowing, UUID> {

    Flux<UserFollowing> findByUserId(UUID userId, Pageable pageable);

    Flux<UserFollowing> findByFollowingId(UUID followingId, Pageable pageable);

    Mono<UserFollowing> findByUserIdAndFollowingId(UUID userId, UUID followingId);

    Flux<UserFollowing> findByFollowingId(UUID userId);
}
