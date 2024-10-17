package com.mobi.ripple_be.controller.post;


import com.mobi.ripple_be.controller.post.reqrespbody.CreatePostRequest;
import com.mobi.ripple_be.controller.post.reqrespbody.GetPostResponse;
import com.mobi.ripple_be.model.PostModel;
import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import com.mobi.ripple_be.service.PathService;
import com.mobi.ripple_be.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/p")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final ConversionService conversionService;
    private final PathService pathService;

    @GetMapping("/{postId}")
    public Mono<ResponseEntity<RespModelImpl<GetPostResponse>>> getPost(@PathVariable(name = "postId") UUID postId) {
        return postService.getPost(postId)
                .mapNotNull(postModel -> conversionService.convert(postModel, GetPostResponse.class))
                .map(postResponse -> ResponseEntity.ok(RespModelImpl.of(postResponse)))
                .defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("Post does not exist"),
                        HttpStatus.NOT_FOUND
                ))
                .onErrorReturn(new ResponseEntity<>(
                        RespModelImpl.serviceUnavailableError(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }

    @GetMapping
    public Mono<ResponseEntity<RespModelImpl<List<GetPostResponse>>>> getUserPosts(
            @RequestParam(name = "authorId") UUID authorId,
            @RequestParam(name = "page") int page
    ) {
        return postService.getUserPosts(authorId, page)
                .mapNotNull(postModel -> conversionService.convert(postModel, GetPostResponse.class))
                .collectList()
                .map(postResponse -> ResponseEntity.ok(RespModelImpl.of(postResponse)))
                .defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("This user has no posts yet"),
                        HttpStatus.NOT_FOUND
                ))
                .onErrorReturn(new ResponseEntity<>(
                        RespModelImpl.serviceUnavailableError(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> savePost(
            @RequestPart("image") Mono<FilePart> image,
            @RequestPart(value = "caption", required = false) String caption
    ) {
        return pathService.getAuthUserPostsPathMono()
                .map(imagePath -> new CreatePostRequest(image, imagePath, caption))
                .flatMap(createPostRequest -> postService.addPost(
                                Objects.requireNonNull(conversionService.convert(createPostRequest, PostModel.class))
                        )
                ).thenReturn(ResponseEntity.ok(RespModelImpl.of(true)))
                .defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.unauthorizedError(), HttpStatus.UNAUTHORIZED)
                );
    }

    //TODO: implement remove and update endpoints

    @PutMapping("/{postId}/like")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> likeOrUnlikePost(@PathVariable String postId) {
        return postService.likeOrUnlikePost(postId)
                .thenReturn(ResponseEntity.ok(RespModelImpl.of(true)))
                .defaultIfEmpty(new ResponseEntity<>(RespModelImpl.unauthorizedError(), HttpStatus.UNAUTHORIZED))
                .onErrorReturn(ResponseEntity.internalServerError().body(RespModelImpl.serviceUnavailableError()));
    }
}
