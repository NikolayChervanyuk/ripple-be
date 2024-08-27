package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.exception.ImageNotFoundException;
import com.mobi.ripple_be.model.SimplePostModel;
import com.mobi.ripple_be.service.MediaService;
import com.mobi.ripple_be.view.SimplePostView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class SimplePostViewToSimplePostModel extends BaseConverter<SimplePostView, SimplePostModel> {

    private final MediaService mediaService;

    @Override
    public SimplePostModel convert(SimplePostView source) {

        Optional<byte[]> image = mediaService.getImage(source.getPostImageDir(), source.getId() + ".jpg");

        return SimplePostModel.builder()
                .id(source.getId())
                .image(image.orElseThrow(() ->
                                new ImageNotFoundException(
                                        "Image " + source.getPostImageDir() + "/" + source.getId() + " not found"
                                )
                        )
                )
                .authorId(source.getAuthorId())
                .build();
    }
}
