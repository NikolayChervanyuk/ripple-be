package com.mobi.ripple_be.service;

import com.mobi.ripple_be.entity.Post;
import com.mobi.ripple_be.entity.PostLike;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.model.PostModel;
import com.mobi.ripple_be.repository.PostLikeRepository;
import com.mobi.ripple_be.repository.PostRepository;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import com.mobi.ripple_be.util.Authorable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService implements Authorable {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final ConversionService conversionService;
    private final MediaService mediaService;
    private final UserRepository userRepository;

    @Value("${ripple.page-size.user-posts}")
    private Integer USER_POSTS_PAGE_SIZE;

    public Mono<PostModel> getPost(UUID postId) {
        return AuthPrincipalProvider
                .getAuthenticatedUserUUIDMono()
                .flatMap(userId -> postRepository.getPostById(postId)
                        .mapNotNull(post -> conversionService.convert(post, PostModel.class))
                        .flatMap(postModel -> setPostLikedState(postModel, userId))
                );
    }

    public Flux<PostModel> getUserPosts(UUID authorId, int page) {
        return AuthPrincipalProvider
                .getAuthenticatedUserUUIDMono()
                .flux()
                .flatMap(userId -> userRepository.getAppUserViewById(authorId)
                        .mapNotNull(authorView -> conversionService.convert(authorView, AppUserModel.class))
                        .flux().flatMap(authorModel ->
                                postRepository.getUserPostsByAuthorIdOrderByCreationDateDesc(
                                                authorId,
                                                PageRequest.of(page, USER_POSTS_PAGE_SIZE)
                                        )
                                        .mapNotNull(post -> conversionService.convert(post, PostModel.class))
                                        .flatMap(postModel -> setPostLikedState(postModel, userId))
                                        .map(postModel -> setAuthorData(postModel, authorModel))
                        )

                );
    }

    public Mono<Boolean> addPost(PostModel postModel) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> userRepository.findById(UUID.fromString(userId)))
                .flatMap(user -> {
                    user.setPostsCount(user.getPostsCount() + 1);
                    return userRepository.save(user);
                })
                .then(postRepository.save(Objects.requireNonNull(conversionService.convert(postModel, Post.class))))
                .flatMap(savedPost -> mediaService.storePostImage(postModel.getImageFile(), savedPost.getId().toString()));
    }

//    @PreAuthorize("#{postService.isAuthorOf(postId)}")
//    public Mono<Boolean> deletePost(UUID postId) {
//        return postRepository.deletePostById(postId)
//                .doOnNext(postCommentRepository.delete)
//                .thenReturn(true)
//                .onErrorReturn(false);
//    }

    @Transactional
    public Mono<Boolean> likeOrUnlikePost(String postId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> postRepository.findById(UUID.fromString(postId))
                        .flatMap(post -> postLikeRepository
                                .findByParentPostIdAndAuthorId(
                                        UUID.fromString(postId),
                                        UUID.fromString(userId)
                                )
                                .flatMap(pl -> { // should unlike
                                    post.setLikesCount(post.getLikesCount() - 1);
                                    return postRepository.save(post);
                                })
                                .flatMap(p -> postLikeRepository.deleteByAuthorIdAndParentPostId(
                                        UUID.fromString(userId),
                                        UUID.fromString(postId)
                                )).switchIfEmpty(Mono.defer(() -> { // should like
                                                    post.setLikesCount(post.getLikesCount() + 1);
                                                    return postRepository.save(post);
                                                })
                                                .flatMap(p -> postLikeRepository.save(
                                                        PostLike.builder()
                                                                .parentPostId(UUID.fromString(postId))
                                                                .authorId(UUID.fromString(userId))
                                                                .build())
                                                ).thenReturn(true)
                                )
                                .doOnError(Throwable::printStackTrace)
                                .onErrorReturn(false)
                        )
                );
    }

    @Override
    public Mono<Boolean> isAuthorized(String postId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> postRepository
                        .getPostById(UUID.fromString(postId))
                        .map(post -> post.getAuthorId().toString().equals(userId))
                );
    }

    private Mono<PostModel> setPostLikedState(PostModel postModel, UUID likeAuthorId) {
        return postLikeRepository
                .findByParentPostIdAndAuthorId(
                        postModel.getId(),
                        likeAuthorId
                )
                .doOnNext(pl -> postModel.setLikedByUser(true))
                .thenReturn(postModel);
    }

    private PostModel setAuthorData(PostModel postModel, AppUserModel authorModel) {
        postModel.setAuthorFullName(authorModel.getFullName());
        postModel.setAuthorUsername(authorModel.getUsername());
        postModel.setAuthorActive(authorModel.isActive());
        return postModel;
    }
}
