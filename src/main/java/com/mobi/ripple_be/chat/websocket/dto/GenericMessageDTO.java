package com.mobi.ripple_be.chat.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.MessageContentDTO;
import com.mobi.ripple_be.chat.websocket.util.ChatEventType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class GenericMessageDTO<C extends MessageContentDTO> implements Serializable {

    @JsonProperty("eventType")
    private ChatEventType eventType;

    @JsonProperty("sentDate")
    private Instant sentDate;

    @JsonProperty("messageData")
    private C content;

}
