package com.mobi.ripple_be.service;

import com.mobi.ripple_be.entity.CommentLike;
import com.mobi.ripple_be.entity.PostComment;
import com.mobi.ripple_be.model.PostCommentModel;
import com.mobi.ripple_be.repository.*;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import com.mobi.ripple_be.util.Authorable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommentService implements Authorable {

    private final CommentReplyRepository commentReplyRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final MediaService mediaService;
    private final PathService pathService;
    @Value("${ripple.page-size.post-comments}")
    private Integer POST_COMMENTS_PAGE_SIZE;

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ConversionService conversionService;

    public Mono<PostCommentModel> getTopComment(String parentPostId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> postCommentRepository.getTopPostComment(UUID.fromString(parentPostId))
                        .mapNotNull(postCommentView -> conversionService.convert(postCommentView, PostCommentModel.class))
                        .flatMap(commentModel -> isCommentLiked(UUID.fromString(commentModel.getId()))
                                .doOnNext(commentModel::setLikedByUser)
                                .thenReturn(commentModel)
                        )
                );
    }

    public Flux<PostCommentModel> getTopComments(String parentPostId, int page) {
        return postCommentRepository.getTopPostComments(
                        UUID.fromString(parentPostId),
                        page * POST_COMMENTS_PAGE_SIZE,
                        POST_COMMENTS_PAGE_SIZE
                ).mapNotNull(postCommentView -> conversionService.convert(postCommentView, PostCommentModel.class))
                .flatMap(commentModel -> isCommentLiked(UUID.fromString(commentModel.getId()))
                        .doOnNext(commentModel::setLikedByUser)
                        .thenReturn(commentModel)
                );
    }

    @Transactional
    public Mono<Boolean> addPostComment(String parentPostId, @Valid @Size(min = 1, max = 4096) String comment) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .map(userId -> PostCommentModel.builder()
                        .parentPostId(parentPostId)
                        .authorId(userId)
                        .likesCount(0L)
                        .repliesCount(0L)
                        .comment(comment)
                        .build()
                )
                .flatMap(postCommentModel ->
                        postCommentRepository.save(
                                Objects.requireNonNull(conversionService.convert(postCommentModel, PostComment.class))
                        )
                )
                .flatMap(postComment -> postRepository
                        .getPostById(UUID.fromString(parentPostId))
                        .flatMap(post -> {
                            post.setCommentsCount(post.getCommentsCount() + 1);
                            return postRepository.save(post);
                        })
                )
                .map(postComment -> true);
    }

    @PreAuthorize("@postCommentService.isAuthorOf(#commentId)")
    public Mono<Boolean> editPostComment(String parentPostId, String commentId, PostCommentModel postCommentModel) {

        return postCommentRepository.getPostCommentByParentPostIdAndId(
                        UUID.fromString(parentPostId),
                        UUID.fromString(commentId)
                )
                .flatMap(postComment -> {
                    postComment.setComment(postCommentModel.getComment());
                    return postCommentRepository.save(postComment).thenReturn(true);
                })
                .doOnError(Throwable::printStackTrace);
    }

    //TODO: test
    @Transactional
    @PreAuthorize("@postCommentService.isAuthorOf(#commentId)")
    public Mono<Boolean> deletePostComment(String parentPostId, UUID commentId) {
        return postRepository
                .getPostById(UUID.fromString(parentPostId))
                .flatMap(post -> {
                    post.setCommentsCount(post.getCommentsCount() - 1);
                    return postRepository.save(post).thenReturn(true);
                })
                .then(postCommentRepository.deleteById(commentId))
                .then(commentLikeRepository.deleteByParentCommentId(commentId))
                .flux()
                .flatMap(v -> commentReplyRepository.findByParentCommentId(commentId))
                .flatMap(commentReply -> commentReplyRepository.deleteById(commentReply.getId()).thenReturn(commentReply))
                .flatMap(commentReply -> replyLikeRepository.deleteByParentReplyId(commentReply.getId()))
                .then().thenReturn(true);
    }

    @Transactional
    public Mono<Boolean> likeOrUnlikePostComment(String parentPostId, String commentId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> postCommentRepository
                        .getPostCommentByParentPostIdAndId(
                                UUID.fromString(parentPostId),
                                UUID.fromString(commentId)
                        ).flatMap(postComment -> commentLikeRepository
                                .findByAuthorIdAndParentCommentId(
                                        UUID.fromString(userId),
                                        UUID.fromString(commentId)
                                ).flatMap(commentLike -> { //should unlike
                                    postComment.setLikesCount(postComment.getLikesCount() - 1);
                                    return postCommentRepository.save(postComment);
                                })
                                .flatMap(pc -> commentLikeRepository
                                        .deleteByAuthorIdAndParentCommentId(
                                                UUID.fromString(userId),
                                                UUID.fromString(commentId)
                                        )
                                ).switchIfEmpty(Mono.defer(() -> { //should like
                                            postComment.setLikesCount(postComment.getLikesCount() + 1);
                                            return postCommentRepository.save(postComment);
                                        }).then(commentLikeRepository.save(
                                                        CommentLike.builder()
                                                                .parentCommentId(UUID.fromString(commentId))
                                                                .authorId(UUID.fromString(userId))
                                                                .build()
                                                )
                                        ).thenReturn(true)
                                )
                        )
                ).thenReturn(true);
    }

    @Override
    public Mono<Boolean> isAuthorOf(String commentId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> postCommentRepository.findById(UUID.fromString(commentId))
                        .map(postComment -> postComment.getAuthorId().toString().equals(userId)));
    }

    private Mono<Boolean> isCommentLiked(UUID commentId) {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userId -> commentLikeRepository
                        .findByAuthorIdAndParentCommentId(
                                userId,
                                commentId
                        )
                )
                .map(v -> true)
                .defaultIfEmpty(false);
    }
}
