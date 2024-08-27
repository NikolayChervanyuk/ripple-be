package com.mobi.ripple_be.chat.entity.postgres;

import com.mobi.ripple_be.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = @Index(columnList = "chatId, userId"))
public class ChatUser extends BaseEntity {

    @Column(nullable = false)
    private UUID chatId;

    @Column(nullable = false)
    private UUID userId;
}
