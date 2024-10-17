package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.post.reqrespbody.GetPostResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.PostModel;
import com.mobi.ripple_be.service.MediaService;
import com.mobi.ripple_be.service.PathService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@AllArgsConstructor
public class PostModelToGetPostResponse extends BaseConverter<PostModel, GetPostResponse> {

    private final MediaService mediaService;
    private final PathService pathService;

    @Override
    public GetPostResponse convert(PostModel source) {

        byte[] postImage = mediaService.getImage(
                Path.of(source.getPostImageDir(), source.getId().toString() + ".jpg")
        ).orElse(null);

        byte[] authorPfpImage = mediaService.getImage(
                pathService.getSmallUserProfilePictureFilePath(source.getAuthorId().toString())
        ).orElse(null);

        return GetPostResponse.builder()
                .id(source.getId())
                .authorId(source.getAuthorId())
                .authorFullName(source.getAuthorFullName())
                .authorUsername(source.getAuthorUsername())
                .isAuthorActive(source.isAuthorActive())
                .authorSmallProfilePicture(authorPfpImage)
                .postImage(postImage)
                .caption(source.getCaption())
                .commentsCount(source.getCommentsCount())
                .likesCount(source.getLikesCount())
                .liked(source.isLikedByUser())
                .creationDate(source.getCreationDate())
                .lastModifiedDate(source.getLastModifiedDate())
                .build();
    }
}
