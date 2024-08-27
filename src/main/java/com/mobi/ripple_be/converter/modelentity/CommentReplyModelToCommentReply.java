package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.entity.CommentReply;
import com.mobi.ripple_be.model.CommentReplyModel;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommentReplyModelToCommentReply extends BaseConverter<CommentReplyModel, CommentReply> {

    @Override
    public CommentReply convert(CommentReplyModel source) {
        var commentReply = new CommentReply();
        if(source.getId() != null) commentReply.setId(UUID.fromString(source.getId()));
        commentReply.setParentCommentId(UUID.fromString(source.getParentCommentId()));
        commentReply.setAuthorId(UUID.fromString(source.getAuthorId()));
        commentReply.setComment(source.getReply());
        commentReply.setLikesCount(source.getLikesCount());
        return commentReply;
    }
}
