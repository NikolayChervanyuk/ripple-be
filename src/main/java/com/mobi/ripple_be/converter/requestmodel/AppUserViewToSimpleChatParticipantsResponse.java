package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.chat.http.controller.reqresp.SimpleChatParticipantsResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.service.MediaService;
import com.mobi.ripple_be.service.PathService;
import com.mobi.ripple_be.view.AppUserView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@AllArgsConstructor
public class AppUserViewToSimpleChatParticipantsResponse extends BaseConverter<AppUserView, SimpleChatParticipantsResponse> {

    private final MediaService mediaService;
    private final PathService pathService;
    @Override
    public SimpleChatParticipantsResponse convert(AppUserView source) {
        final Path smallPfpPath = pathService
                .getSmallUserProfilePictureFilePath(source.getId().toString());
        return SimpleChatParticipantsResponse.builder()
                .participantId(source.getId().toString())
                .name(source.getFullName())
                .username(source.getUsername())
                .isActive(source.getIsActive())
                .lastActive(source.getLastActive())
                .smallProfilePicture(mediaService.getImage(smallPfpPath)
                        .orElse(null)
                )
                .build();
    }
}
