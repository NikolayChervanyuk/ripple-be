package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.CommentReplyModel;
import com.mobi.ripple_be.view.CommentReplyView;
import org.springframework.stereotype.Component;

@Component
public class CommentReplyViewToCommentReplyModel extends BaseConverter<CommentReplyView, CommentReplyModel> {
    @Override
    public CommentReplyModel convert(CommentReplyView source) {
        return CommentReplyModel.builder()
                .id(source.getId())
                .parentCommentId(source.getParentCommentId())
                .authorId(source.getAuthorId())
                .authorName(source.getAuthorName())
                .authorUsername(source.getAuthorUsername())
                .creationDate(source.getCreatedDate())
                .lastModifiedDate(source.getLastUpdatedDate())
                .likesCount(source.getLikesCount())
                .reply(source.getReply())
                .build();
    }
}
