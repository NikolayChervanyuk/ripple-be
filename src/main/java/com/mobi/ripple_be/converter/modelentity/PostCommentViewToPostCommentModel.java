package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.PostCommentModel;
import com.mobi.ripple_be.view.PostCommentView;
import org.springframework.stereotype.Component;

@Component
public class PostCommentViewToPostCommentModel extends BaseConverter<PostCommentView, PostCommentModel> {
    @Override
    public PostCommentModel convert(PostCommentView source) {
        return PostCommentModel.builder()
                .id(source.getId())
                .parentPostId(source.getParentPostId())
                .authorId(source.getAuthorId())
                .authorName(source.getAuthorName())
                .authorUsername(source.getAuthorUsername())
                .creationDate(source.getCreationDate())
                .lastModifiedDate(source.getLastModifiedDate())
                .likesCount(source.getLikesCount())
                .repliesCount(source.getRepliesCount())
                .comment(source.getComment())
                .build();
    }
}
