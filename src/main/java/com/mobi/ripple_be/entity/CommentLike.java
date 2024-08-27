package com.mobi.ripple_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = @Index(columnList = "authorId"))
public class CommentLike extends BaseEntity {

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private UUID parentCommentId;
}