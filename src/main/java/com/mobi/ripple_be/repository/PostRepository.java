package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.Post;
import com.mobi.ripple_be.view.SimplePostView;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PostRepository extends R2dbcRepository<Post, UUID> {

    @Query("SELECT p.id, p.post_image_dir, p.author_id " +
            "FROM app_user AS u " +
            "INNER JOIN post AS p " +
            "ON p.author_id = u.id " +
            "WHERE u.username = :username " +
            "ORDER BY p.last_modified_date DESC " +
            "OFFSET :offset " +
            "LIMIT :size")
    Flux<SimplePostView> getSimpleUserPostsByUsername(String username, int offset, int size);

    Mono<Post> getPostById(UUID id);
}
