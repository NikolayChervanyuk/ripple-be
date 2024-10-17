package com.mobi.ripple_be.chat.http.controller.reqresp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class SimpleChatParticipantsResponse {
    private String participantId;
    private String name;
    private String username;
    private boolean isActive;
    private Instant lastActive;
    private byte[] smallProfilePicture;
}
