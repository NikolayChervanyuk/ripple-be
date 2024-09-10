package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.comment.post.reqrespbody.GetPostCommentResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.PostCommentModel;
import org.springframework.stereotype.Component;

@Component
public class PostCommentModelToGetPostCommentResponse extends
        BaseConverter<PostCommentModel, GetPostCommentResponse> {
    @Override
    public GetPostCommentResponse convert(PostCommentModel source) {
        return GetPostCommentResponse.builder()
                .commentId(source.getId())
                .authorProfilePicture(source.getAuthorProfilePicture())
                .authorName(source.getAuthorName())
                .authorUsername(source.getAuthorUsername())
                .createdDate(source.getCreationDate())
                .lastUpdatedDate(source.getLastModifiedDate())
                .likesCount(source.getLikesCount())
                .repliesCount(source.getRepliesCount())
                .liked(source.isLikedByUser())
                .comment(source.getComment())
                .build();
    }
}
