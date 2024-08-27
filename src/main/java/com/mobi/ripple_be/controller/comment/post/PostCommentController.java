package com.mobi.ripple_be.controller.comment.post;

import com.mobi.ripple_be.controller.comment.post.reqrespbody.AddPostCommentRequest;
import com.mobi.ripple_be.controller.comment.post.reqrespbody.EditPostCommentRequest;
import com.mobi.ripple_be.controller.comment.post.reqrespbody.GetPostCommentResponse;
import com.mobi.ripple_be.model.PostCommentModel;
import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import com.mobi.ripple_be.service.PostCommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/p/{postId}/comments")
public class PostCommentController {

    private final PostCommentService postCommentService;
    private final ConversionService conversionService;

    @GetMapping
    public Mono<ResponseEntity<RespModelImpl<GetPostCommentResponse>>> getTopPostComment(@PathVariable String postId) {
        return postCommentService.getTopComment(postId)
                .mapNotNull(commentModel ->
                        ResponseEntity.ok(RespModelImpl.of(
                                conversionService.convert(commentModel, GetPostCommentResponse.class)

                        ))
                ).defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("Post not found"),
                        HttpStatus.NOT_FOUND
                ));
    }

    @GetMapping(params = "page")
    public Mono<RespModelImpl<List<GetPostCommentResponse>>> getTopPostComments(@PathVariable String postId,
                                                                                @RequestParam(name = "page") int page
    ) {
        return postCommentService.getTopComments(postId, page)
                .mapNotNull(commentModel -> conversionService.convert(commentModel, GetPostCommentResponse.class))
                .collectList()
                .map(RespModelImpl::of);
    }

    @PostMapping
    public Mono<RespModelImpl<Boolean>> addPostComment(@PathVariable String postId,
                                                       @RequestBody final AddPostCommentRequest commentRequest
    ) {
        return postCommentService.addPostComment(postId, commentRequest.getComment())
                .thenReturn(RespModelImpl.of(true))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @PutMapping
    public Mono<RespModelImpl<Boolean>> editPostComment(@PathVariable String postId,
                                                        @RequestParam("id") String commentId,
                                                        @Valid @RequestBody final EditPostCommentRequest editCommentRequest
    ) {
        return postCommentService.editPostComment(postId, commentId,
                        Objects.requireNonNull(conversionService.convert(editCommentRequest, PostCommentModel.class))
                )
                .thenReturn(RespModelImpl.of(true))
                .defaultIfEmpty(RespModelImpl.ofError("Post editing failed, post does not exist"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @DeleteMapping
    public Mono<RespModelImpl<Boolean>> deletePostComment(@PathVariable String postId,
                                                          @RequestParam("id") String commentId
    ) {
        return postCommentService.deletePostComment(postId, UUID.fromString(commentId))
                .thenReturn(RespModelImpl.of(true))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @PutMapping("/{commentId}/like")
    public Mono<RespModelImpl<Boolean>> likeOrUnlikePostComment(@PathVariable String postId,
                                                                @PathVariable String commentId
    ) {
        return postCommentService.likeOrUnlikePostComment(postId, commentId)
                .thenReturn(RespModelImpl.of(true))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }
}
