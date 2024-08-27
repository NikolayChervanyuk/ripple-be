package com.mobi.ripple_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(indexes = @Index(columnList = "parentCommentId"))
public class CommentReply extends BaseComment {

    @Column(nullable = false)
    private UUID parentCommentId;
}