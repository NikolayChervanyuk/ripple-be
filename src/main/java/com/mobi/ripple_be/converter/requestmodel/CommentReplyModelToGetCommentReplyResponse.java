package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.comment.reply.reqrespbody.GetCommentReplyResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.CommentReplyModel;
import com.mobi.ripple_be.service.MediaService;
import com.mobi.ripple_be.service.PathService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@AllArgsConstructor
public class CommentReplyModelToGetCommentReplyResponse extends BaseConverter<CommentReplyModel, GetCommentReplyResponse> {

    private final MediaService mediaService;
    private final PathService pathService;

    @Override
    public GetCommentReplyResponse convert(CommentReplyModel source) {
        final Path smallPfpPath = pathService
                .getSmallUserProfilePictureFilePath(source.getAuthorId());

        return GetCommentReplyResponse.builder()
                .id(source.getId())
                .authorProfilePicture(mediaService.getImage(smallPfpPath)
                        .orElse(null)
                )
                .authorName(source.getAuthorName())
                .authorUsername(source.getAuthorUsername())
                .createdDate(source.getCreationDate())
                .lastUpdatedDate(source.getLastModifiedDate())
                .likesCount(source.getLikesCount())
                .isLiked(source.isLikedByUser())
                .reply(source.getReply())
                .build();
    }
}
