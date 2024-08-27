package com.mobi.ripple_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SimplePostModel {
    private String id;
    private byte[] image;
    private String authorId;
}
