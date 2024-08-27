package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.entity.Post;
import com.mobi.ripple_be.model.PostModel;
import org.springframework.stereotype.Component;

@Component
public class PostModelToPost extends BaseConverter<PostModel, Post> {

    @Override
    public Post convert(PostModel source) {
        var post = new Post();
        if (source.getId() != null) post.setId(source.getId());
        post.setAuthorId(source.getAuthorId());
        post.setPostImageDir(source.getPostImageDir());
        post.setCaption(source.getCaption());
        post.setCommentsCount(source.getCommentsCount());
        post.setLikesCount(source.getLikesCount());

        return post;
    }
}
