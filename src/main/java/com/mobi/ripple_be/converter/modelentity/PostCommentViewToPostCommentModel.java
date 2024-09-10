package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.PostCommentModel;
import com.mobi.ripple_be.service.MediaService;
import com.mobi.ripple_be.service.PathService;
import com.mobi.ripple_be.view.PostCommentView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostCommentViewToPostCommentModel extends BaseConverter<PostCommentView, PostCommentModel> {
    private final MediaService mediaService;
    private final PathService pathService;

    @Override
    public PostCommentModel convert(PostCommentView source) {
        var authorPfp = mediaService.getImage(pathService
                .getSmallUserProfilePictureFilePath(source.getAuthorId()
                ));
        return PostCommentModel.builder()
                .id(source.getId())
                .parentPostId(source.getParentPostId())
                .authorProfilePicture(authorPfp.orElse(null))
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
