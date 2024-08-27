package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.comment.reply.reqrespbody.AddCommentReplyRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.CommentReplyModel;
import org.springframework.stereotype.Component;

@Component
public class AddCommentReplyRequestToCommentReplyModel extends BaseConverter<AddCommentReplyRequest, CommentReplyModel> {
    @Override
    public CommentReplyModel convert(AddCommentReplyRequest source) {
        return CommentReplyModel.builder()
                .reply(source.getReply())
                .build();
    }
}
