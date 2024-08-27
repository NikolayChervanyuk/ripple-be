package com.mobi.ripple_be.controller.post.reqrespbody;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Getter
@Setter
@AllArgsConstructor
public class CreatePostRequest {

    private Mono<FilePart> imageFile;
    private Path imageUrl;
    private String caption;
}
