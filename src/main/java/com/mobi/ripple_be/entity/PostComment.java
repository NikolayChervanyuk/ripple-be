package com.mobi.ripple_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@Entity
@Table(indexes = @Index(columnList = "parentPostId"))
public class PostComment extends BaseComment {

    @Column(nullable = false)
    private UUID parentPostId;

    @Column(nullable = false)
    private Long repliesCount;
}