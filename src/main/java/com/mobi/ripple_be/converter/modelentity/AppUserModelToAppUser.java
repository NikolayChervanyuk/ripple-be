package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.entity.AppUser;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.util.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class AppUserModelToAppUser extends BaseConverter<AppUserModel, AppUser> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser convert(AppUserModel source) {
        var appUser = new AppUser();
        if(source.getId() != null) appUser.setId(UUID.fromString(source.getId()));
        appUser.setFullName(source.getFullName());
        appUser.setEmail(source.getEmail());
        appUser.setUsername(source.getUsername());
        appUser.setPassword(passwordEncoder.encode(source.getPassword()));
        appUser.setFollowers(source.getFollowers());
        appUser.setFollowing(source.getFollowing());
        appUser.setPostsCount(source.getPostsCount());
        appUser.setRole(Role.USER);
        appUser.setLastIssuedTokenRevocation(source.getLastIssuedTokenRevocation());

        return appUser;
    }
}
