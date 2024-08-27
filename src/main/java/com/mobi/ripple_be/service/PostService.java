package com.mobi.ripple_be.service;

import com.mobi.ripple_be.entity.Post;
import com.mobi.ripple_be.entity.PostLike;
import com.mobi.ripple_be.model.PostModel;
import com.mobi.ripple_be.repository.PostCommentRepository;
import com.mobi.ripple_be.repository.PostLikeRepository;
import com.mobi.ripple_be.repository.PostRepository;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import com.mobi.ripple_be.util.Authorable;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class PostService implements Authorable {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PathService pathService;
    private final ConversionService conversionService;
    private final MediaService mediaService;
    private final UserRepository userRepository;

    public Mono<PostModel> getPost(UUID postId) {
        return AuthPrincipalProvider
                .getAuthenticatedUserIdMono()
                .flatMap(userId -> postRepository.getPostById(postId)
                        .mapNotNull(post -> conversionService.convert(post, PostModel.class))
                        .flatMap(postModel -> postLikeRepository
                                .findByParentPostIdAndAuthorId(
                                        postId,
                                        UUID.fromString(userId)
                                )
                                .doOnNext(pl -> {
                                    postModel.setLikedByUser(true);
                                })
                                .thenReturn(postModel)
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
    public Mono<Boolean> isAuthorOf(String postId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> postRepository
                        .getPostById(UUID.fromString(postId))
                        .map(post -> post.getAuthorId().toString().equals(userId))
                );
    }
}
