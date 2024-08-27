package com.mobi.ripple_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(indexes = @Index(columnList = "authorId"))
public class Post extends BaseEntity {

    @Column(nullable = false)
    private UUID authorId;

    @Column(length = 512, nullable = false)
    private String postImageDir;

    @Column(length = 2048)
    private String caption;

    @Column(nullable = false)
    private Long likesCount;

    @Column(nullable = false)
    private Long commentsCount;
}