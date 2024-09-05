package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.user.reqrespbody.SimpleUserResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.exception.ImageNotFoundException;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.service.MediaService;
import com.mobi.ripple_be.service.PathService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@AllArgsConstructor
public class AppUserModelToSimpleUserResponse extends BaseConverter<AppUserModel, SimpleUserResponse> {

    private final MediaService mediaService;
    private final PathService pathService;

    @Override
    public SimpleUserResponse convert(AppUserModel source) {
        final Path smallPfpPath = pathService
                .getSmallUserProfilePictureFilePath(source.getId());

        return SimpleUserResponse.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .username(source.getUsername())
                .isActive(source.isActive())
                .smallProfilePicture(mediaService.getImage(smallPfpPath)
                        .orElse(null)
                )
                .build();
    }
}
