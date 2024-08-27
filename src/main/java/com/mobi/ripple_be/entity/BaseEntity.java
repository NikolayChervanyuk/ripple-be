package com.mobi.ripple_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.id.uuid.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id = UUID.randomUUID();

    @Version
    private Integer version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant creationDate;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant lastModifiedDate;
}