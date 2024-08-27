package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.comment.post.reqrespbody.EditPostCommentRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.PostCommentModel;
import org.springframework.stereotype.Component;

@Component
public class EditPostCommentRequestToPostCommentModel extends BaseConverter<EditPostCommentRequest, PostCommentModel> {
    @Override
    public PostCommentModel convert(EditPostCommentRequest source) {
        return PostCommentModel.builder()
//                .id(source.getCommentId())
                .comment(source.getComment())
                .build();
    }
}
