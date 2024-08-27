package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.entity.PostComment;
import com.mobi.ripple_be.model.PostCommentModel;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostCommentModelToPostComment extends BaseConverter<PostCommentModel, PostComment> {
    @Override
    public PostComment convert(PostCommentModel source) {
        var postComment = new PostComment();

        if(source.getId() != null) postComment.setId(UUID.fromString(source.getId()));
        postComment.setParentPostId(UUID.fromString(source.getParentPostId()));
        postComment.setAuthorId(UUID.fromString(source.getAuthorId()));
        postComment.setLikesCount(source.getLikesCount());
        postComment.setRepliesCount(source.getRepliesCount());
        postComment.setComment(source.getComment());

        return postComment;
    }
}
