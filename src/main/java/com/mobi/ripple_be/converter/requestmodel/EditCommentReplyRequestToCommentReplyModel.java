package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.comment.reply.reqrespbody.EditCommentReplyRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.CommentReplyModel;
import org.springframework.stereotype.Component;

@Component
public class EditCommentReplyRequestToCommentReplyModel extends BaseConverter<EditCommentReplyRequest, CommentReplyModel> {
    @Override
    public CommentReplyModel convert(EditCommentReplyRequest source) {
        return CommentReplyModel.builder()
                .reply(source.getReply())
                .build();
    }
}
