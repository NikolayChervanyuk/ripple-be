package com.mobi.ripple_be.service;

import com.mobi.ripple_be.entity.CommentReply;
import com.mobi.ripple_be.entity.ReplyLike;
import com.mobi.ripple_be.model.CommentReplyModel;
import com.mobi.ripple_be.repository.CommentReplyRepository;
import com.mobi.ripple_be.repository.PostCommentRepository;
import com.mobi.ripple_be.repository.ReplyLikeRepository;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import com.mobi.ripple_be.util.Authorable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentReplyService implements Authorable {

    @Value("${ripple.page-size.comment-replies}")
    private Integer COMMENT_REPLIES_PAGE_SIZE;

    private final CommentReplyRepository commentReplyRepository;
    private final PostCommentRepository postCommentRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final ConversionService conversionService;


    public Flux<CommentReplyModel> getLatestCommentReplies(@NonNull String parentPostId,
                                                           @NonNull String parentCommentId,
                                                           int page
    ) {
        return commentReplyRepository.getLatestCommentReplies(
                        UUID.fromString(parentCommentId),
                        (COMMENT_REPLIES_PAGE_SIZE * page),
                        COMMENT_REPLIES_PAGE_SIZE)
                .mapNotNull(commentReplyView ->
                        conversionService.convert(commentReplyView, CommentReplyModel.class)
                )
                .flatMap( replyModel -> isReplyLiked(UUID.fromString(replyModel.getId()))
                        .doOnNext(replyModel::setLikedByUser)
                        .thenReturn(replyModel)
                );
    }

    @Transactional
    public Mono<Boolean> addCommentReply(String parentCommentId, CommentReplyModel replyModel) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .map(userId -> CommentReplyModel.builder()
                        .parentCommentId(parentCommentId)
                        .authorId(userId)
                        .likesCount(0L)
                        .reply(replyModel.getReply())
                        .build()
                )
                .mapNotNull(commentReplyModel -> conversionService.convert(commentReplyModel, CommentReply.class))
                .flatMap(commentReplyRepository::save)
                .flatMap(commentReply -> postCommentRepository.findById(commentReply.getParentCommentId()))
                .flatMap(postComment -> {
                    postComment.setRepliesCount(postComment.getRepliesCount() + 1);
                    return postCommentRepository.save(postComment);
                }).map(postComment -> true);
    }

    @PreAuthorize("@commentReplyService.isAuthorized(#replyId)")
    public Mono<Boolean> editCommentReply(String commentId, String replyId, CommentReplyModel replyModel) {
        return commentReplyRepository
                .findById(UUID.fromString(replyId))
                .flatMap(commentReply -> {
                    commentReply.setComment(replyModel.getReply());
                    return commentReplyRepository.save(commentReply);
                })
                .thenReturn(true);
    }

    //TODO: test
    @PreAuthorize("@commentReplyService.isAuthorized(#replyId)")
    public Mono<Boolean> deleteCommentReply(String postId, String commentId, String replyId) {

        return replyLikeRepository.deleteByParentReplyId(UUID.fromString(replyId))
                .then(commentReplyRepository.deleteById(UUID.fromString(replyId)))
                .thenReturn(true);
    }

    @Transactional
    public Mono<Boolean> likeOrUnlikeCommentReply(String postId, String commentId, String replyId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> replyLikeRepository
                        .findByAuthorIdAndParentReplyId(
                                UUID.fromString(userId),
                                UUID.fromString(replyId)
                        )
                        .flatMap(replyLike -> //should unlike
                                commentReplyRepository.findById(UUID.fromString(replyId))
                                        .flatMap(commentReply -> {
                                            commentReply.setLikesCount(commentReply.getLikesCount() - 1);
                                            return commentReplyRepository.save(commentReply);
                                        })
                                        .then(replyLikeRepository.delete(replyLike))
                                        .thenReturn(true)
                        )
                        .switchIfEmpty(Mono.defer(() -> //should like
                                commentReplyRepository.findById(UUID.fromString(replyId))
                                        .flatMap(commentReply -> {
                                            commentReply.setLikesCount(commentReply.getLikesCount() + 1);
                                            return commentReplyRepository.save(commentReply);
                                        })
                                        .then(replyLikeRepository.save(
                                                ReplyLike.builder()
                                                        .authorId(UUID.fromString(userId))
                                                        .parentReplyId(UUID.fromString(replyId))
                                                        .build())
                                        )
                                        .thenReturn(true)
                        ))
                );
    }

    @Override
    public Mono<Boolean> isAuthorized(String replyId) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> commentReplyRepository.findById(UUID.fromString(replyId))
                        .map(commentReply -> commentReply.getAuthorId().toString().equals(userId))
                );
    }

    private Mono<Boolean> isReplyLiked(UUID replyId) {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userId -> replyLikeRepository
                        .findByAuthorIdAndParentReplyId(
                                userId,
                                replyId
                        )
                ).hasElement();
    }
}
