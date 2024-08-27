package com.mobi.ripple_be.chat.entity.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PendingMessageUser {
    @Id
    private String msgId;
    private String userId;
}
