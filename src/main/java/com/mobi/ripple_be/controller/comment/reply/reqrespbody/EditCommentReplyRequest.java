package com.mobi.ripple_be.controller.comment.reply.reqrespbody;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditCommentReplyRequest {

    @NotNull
    @Size(min = 1, max = 4096)
    private String reply;

}
