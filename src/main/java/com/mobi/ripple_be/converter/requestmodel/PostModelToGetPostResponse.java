package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.post.reqrespbody.GetPostResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.PostModel;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class PostModelToGetPostResponse extends BaseConverter<PostModel, GetPostResponse> {

    @Override
    public GetPostResponse convert(PostModel source) {

        byte[] image;
        try (var imageStream = new FileInputStream(
                Path.of(source.getPostImageDir(), source.getId().toString() + ".jpg").toString())
        ) {
            image = imageStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return GetPostResponse.builder()
                .id(source.getId())
                .authorId(source.getAuthorId())
                .postImage(image)
                .caption(source.getCaption())
                .commentsCount(source.getCommentsCount())
                .likesCount(source.getLikesCount())
                .liked(source.isLikedByUser())
                .creationDate(source.getCreationDate())
                .lastModifiedDate(source.getLastModifiedDate())
                .build();
    }
}
