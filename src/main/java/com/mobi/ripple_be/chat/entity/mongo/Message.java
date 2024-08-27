package com.mobi.ripple_be.chat.entity.mongo;

import com.mobi.ripple_be.chat.websocket.dto.content.MessageContent;
import com.mobi.ripple_be.chat.websocket.util.ChatEventType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message implements Serializable {

    @Id
    private String msgId;
    @NotNull
    private Instant sentDate;
    @NotNull
    private ChatEventType eventType;
    private MessageContent messageContent;
}
