package com.mobi.ripple_be.chat.entity.postgres;

import com.mobi.ripple_be.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat extends BaseEntity {

    @Column
    private String chatName;

}
