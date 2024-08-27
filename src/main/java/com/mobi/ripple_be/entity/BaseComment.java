package com.mobi.ripple_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class BaseComment extends BaseEntity {

    @Column(length = 4096)
    private String comment;

    @Column(nullable = false)
    private UUID authorId;

//    @Column(nullable = false, unique = true)
//    private  likes;

    @Column
    private Long likesCount;
}