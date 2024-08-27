package com.mobi.ripple_be.controller.comment.post.reqrespbody;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPostCommentRequest {
    @NotNull
    @Size(min = 1, max = 4096)
    private String comment;
}
