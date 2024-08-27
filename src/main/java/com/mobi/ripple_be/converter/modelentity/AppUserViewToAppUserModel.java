package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.view.AppUserView;
import org.springframework.stereotype.Component;

@Component
public class AppUserViewToAppUserModel extends BaseConverter<AppUserView, AppUserModel> {
    @Override
    public AppUserModel convert(AppUserView source) {
        return AppUserModel.builder()
                .id(source.getId().toString())
                .fullName(source.getFullName())
                .email(source.getEmail())
                .username(source.getUsername())
                .bio(source.getBio())
                .followers(source.getFollowers())
                .following(source.getFollowing())
                .isActive(source.getIsActive())
                .lastActive(source.getLastActive())
                .postsCount(source.getPostsCount())
                .lastIssuedTokenRevocation(source.getLastIssuedTokenRevocation())
                .build();
    }
}
