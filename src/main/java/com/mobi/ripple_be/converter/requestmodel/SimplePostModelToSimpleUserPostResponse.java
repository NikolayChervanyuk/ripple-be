package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.user.reqrespbody.SimpleUserPostResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.SimplePostModel;
import org.springframework.stereotype.Component;

@Component
public class SimplePostModelToSimpleUserPostResponse extends BaseConverter<SimplePostModel, SimpleUserPostResponse> {
    @Override
    public SimpleUserPostResponse convert(SimplePostModel source) {
        return SimpleUserPostResponse.builder()
                .id(source.getId())
                .image(source.getImage())
                .authorId(source.getAuthorId())
                .creationDate(source.getCreationDate())
                .build();
    }
}
