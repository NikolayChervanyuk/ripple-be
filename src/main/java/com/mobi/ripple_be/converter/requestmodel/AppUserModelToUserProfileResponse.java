package com.mobi.ripple_be.converter.requestmodel;

import com.mobi.ripple_be.controller.user.reqrespbody.UserProfileResponse;
import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.AppUserModel;
import org.springframework.stereotype.Component;

@Component
public class AppUserModelToUserProfileResponse extends BaseConverter<AppUserModel, UserProfileResponse> {
    @Override
    public UserProfileResponse convert(AppUserModel source) {
        return UserProfileResponse.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .username(source.getUsername())
                .followers(source.getFollowers())
                .following(source.getFollowing())
                .isFollowed(source.isFollowed())
                .isActive(source.isActive())
                .lastActive(source.getLastActive())
                .postsCount(source.getPostsCount())
                .bio(source.getBio())
                .build();
    }
}
