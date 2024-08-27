package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.post.reqrespbody.CreatePostRequest;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.PostModel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreatePostRequestToPostModel extends BaseConverter<CreatePostRequest, PostModel> {

    @Override
    public PostModel convert(@NonNull CreatePostRequest source) {
        var authorId = UUID.fromString(source.getImageUrl().getParent().getFileName().toString());
        return PostModel.builder()
                .postImageDir(source.getImageUrl().toString())
                .imageFile(source.getImageFile())
                .caption(source.getCaption())
                .likesCount(0L)
                .commentsCount(0L)
                .isLikedByUser(false)
                .authorId(authorId)
                .build();
    }
}
