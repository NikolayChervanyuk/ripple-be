package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.entity.Post;
import com.mobi.ripple_be.model.PostModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PostToPostModel extends BaseConverter<Post, PostModel> {

    @Override
    public PostModel convert(Post source) {
        return PostModel.builder()
                .id(source.getId())
                .authorId(source.getAuthorId())
                .postImageDir(source.getPostImageDir())
                .caption(source.getCaption())
                .commentsCount(source.getCommentsCount())
                .likesCount(source.getLikesCount())
                .creationDate(source.getCreationDate())
                .lastModifiedDate(source.getLastModifiedDate())
                .build();
    }
}
