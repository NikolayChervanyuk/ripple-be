package com.mobi.ripple_be.service;

import com.mobi.ripple_be.util.AuthPrincipalProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Service
public class PathService {

    @Value("${ripple.user-data-dir}")
    private String USER_DATA_PATH;

    @Value("${ripple.chat-data-dir}")
    private String CHATS_DATA_PATH;

    public Path getUserPath(String userId) {
        return Path.of(USER_DATA_PATH, userId);
    }

    public Path getUserPostsPath(String userId) {
        return Path.of(USER_DATA_PATH, userId, "posts");
    }

    public Path getSmallUserPostsPath(String userId) {
        return Path.of(USER_DATA_PATH, userId, "posts_small");
    }

    public Mono<Path> getAuthUserPostsPathMono() {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .map(this::getUserPostsPath);
    }

    public Mono<Path> getAuthUserSmallPostsPathMono() {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .map(this::getSmallUserPostsPath);
    }

    public Path getUserProfilePictureFilePath(String userId) {
        return getUserPath(userId).resolve("pfp.jpg");
    }

    public Path getSmallUserProfilePictureFilePath(String userId) {
        return getUserPath(userId).resolve("pfp_small.jpg");
    }

    public Path getChatFilePath(String chatId) {
        return Path.of(CHATS_DATA_PATH, chatId);
    }
}
