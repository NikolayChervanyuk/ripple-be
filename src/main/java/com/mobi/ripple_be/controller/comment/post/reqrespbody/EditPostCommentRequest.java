package com.mobi.ripple_be.controller.comment.post.reqrespbody;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditPostCommentRequest {

    @NotNull
    @Size(min = 1, max = 4096)
    private String comment;
}
