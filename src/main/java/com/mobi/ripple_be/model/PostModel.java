package com.mobi.ripple_be.model;

import lombok.*;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostModel {

    private UUID id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    private UUID authorId;

    private Mono<FilePart> imageFile;

    private String postImageDir;

    private String caption;

    private Long likesCount;

    private boolean isLikedByUser;

    private Long commentsCount;
}
