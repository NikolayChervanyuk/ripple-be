package com.mobi.ripple_be.chat.websocket.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ChatEventType {
    @JsonProperty("new_message")
    NEW_MESSAGE("new_message"),

    @JsonProperty("chat_opened")
    CHAT_OPENED("chat_opened"),

    @JsonProperty("chat_created")
    CHAT_CREATED("chat_created"),

//    @JsonProperty("user_online")
//    USER_ONLINE("user_online"),

    @JsonProperty("new_participant")
    NEW_PARTICIPANT("new_participant"),

    @JsonProperty("participant_left")
    PARTICIPANT_LEFT("participant_left"),

    @JsonProperty("participant_removed")
    PARTICIPANT_REMOVED("participant_removed");


    ChatEventType(String literal) {
        this.literal = literal;
    }

    private final String literal;
}
