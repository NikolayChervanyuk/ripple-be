package com.mobi.ripple_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = @Index(columnList = "authorId"))
public class PostLike extends BaseEntity {

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private UUID parentPostId;
}