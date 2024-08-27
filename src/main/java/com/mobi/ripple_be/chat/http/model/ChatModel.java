package com.mobi.ripple_be.chat.http.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatModel {

    private String id;
    private String name;
    private List<String> participantIds;
    private Instant createdDate;
    private Instant lastUpdatedDate;

}
