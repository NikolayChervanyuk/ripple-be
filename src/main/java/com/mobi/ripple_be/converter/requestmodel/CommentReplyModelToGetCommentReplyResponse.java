package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.comment.reply.reqrespbody.GetCommentReplyResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.CommentReplyModel;
import org.springframework.stereotype.Component;

@Component
public class CommentReplyModelToGetCommentReplyResponse extends BaseConverter<CommentReplyModel, GetCommentReplyResponse> {
    @Override
    public GetCommentReplyResponse convert(CommentReplyModel source) {
        return GetCommentReplyResponse.builder()
                .id(source.getId())
                .authorName(source.getAuthorName())
                .authorUsername(source.getAuthorUsername())
                .createdDate(source.getCreationDate())
                .lastUpdatedDate(source.getLastModifiedDate())
                .likesCount(source.getLikesCount())
                .reply(source.getReply())
                .build();
    }
}
