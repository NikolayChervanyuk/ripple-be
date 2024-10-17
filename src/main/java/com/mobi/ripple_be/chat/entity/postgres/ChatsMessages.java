package com.mobi.ripple_be.chat.entity.postgres;

import com.mobi.ripple_be.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatsMessages extends BaseEntity {
    @Column(nullable = false)
    private UUID chatId;
    @Column(nullable = false)
    private String msgId;
}
