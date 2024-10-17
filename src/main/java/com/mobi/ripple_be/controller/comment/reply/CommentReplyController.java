package com.mobi.ripple_be.controller.comment.reply;

import com.mobi.ripple_be.controller.comment.reply.reqrespbody.AddCommentReplyRequest;
import com.mobi.ripple_be.controller.comment.reply.reqrespbody.EditCommentReplyRequest;
import com.mobi.ripple_be.controller.comment.reply.reqrespbody.GetCommentReplyResponse;
import com.mobi.ripple_be.model.CommentReplyModel;
import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import com.mobi.ripple_be.service.CommentReplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/p/{postId}/comments/{commentId}/replies")
public class CommentReplyController {

    private final CommentReplyService commentReplyService;
    private final ConversionService conversionService;

    @GetMapping
    public Mono<RespModelImpl<List<GetCommentReplyResponse>>> getLatestCommentReplies(@PathVariable String postId,
                                                                                      @PathVariable String commentId,
                                                                                      @RequestParam int page
    ) {
        return commentReplyService.getLatestCommentReplies(postId, commentId, page)
                .mapNotNull(commentReplyModel ->
                        conversionService.convert(commentReplyModel, GetCommentReplyResponse.class)
                ).collectList()
                .map(RespModelImpl::of)
                .defaultIfEmpty(RespModelImpl.ofError("No more comments"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @PostMapping
    public Mono<RespModelImpl<Boolean>> addCommentReply(@PathVariable String postId,
                                                        @PathVariable String commentId,
                                                        @RequestBody AddCommentReplyRequest replyRequest
    ) {
        return commentReplyService
                .addCommentReply(commentId, conversionService.convert(replyRequest, CommentReplyModel.class))
                .thenReturn(RespModelImpl.of(true))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @PutMapping
    public Mono<RespModelImpl<Boolean>> editCommentReply(@PathVariable String postId,
                                                         @PathVariable String commentId,
                                                         @RequestParam("id") String replyId,
                                                         @RequestBody EditCommentReplyRequest replyRequest
    ) {
        return commentReplyService.editCommentReply(
                        commentId,
                        replyId,
                        Objects.requireNonNull(conversionService.convert(replyRequest, CommentReplyModel.class))
                )
                .thenReturn(RespModelImpl.of(true))
                .defaultIfEmpty(RespModelImpl.ofError("Comment does not exist"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @DeleteMapping
    public Mono<RespModelImpl<Boolean>> deleteCommentReply(@PathVariable String postId,
                                                           @PathVariable String commentId,
                                                           @RequestParam(name = "id") String replyId
    ) {
        return commentReplyService.deleteCommentReply(postId, commentId, replyId)
                .thenReturn(RespModelImpl.of(true))
                .defaultIfEmpty(RespModelImpl.ofError("Reply does not exist"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @PutMapping("/{replyId}/like")
    public Mono<RespModelImpl<Boolean>> likeOrUnlikeCommentReply(@PathVariable String postId,
                                                                 @PathVariable String commentId,
                                                                 @PathVariable String replyId
    ) {
        return commentReplyService.likeOrUnlikeCommentReply(postId, commentId, replyId)
                .thenReturn(RespModelImpl.of(true))
                .defaultIfEmpty(RespModelImpl.ofError("Reply does not exist"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }
}
