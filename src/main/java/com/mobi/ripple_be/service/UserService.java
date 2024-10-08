package com.mobi.ripple_be.service;

import com.mobi.ripple_be.entity.AppUser;
import com.mobi.ripple_be.entity.UserFollowing;
import com.mobi.ripple_be.exception.ImageNotFoundException;
import com.mobi.ripple_be.exception.ProfilePictureDeletionException;
import com.mobi.ripple_be.model.AppUserCredentialsModel;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.model.SimplePostModel;
import com.mobi.ripple_be.repository.PostRepository;
import com.mobi.ripple_be.repository.UserFollowingRepository;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import com.mobi.ripple_be.util.IdentifierType;
import com.mobi.ripple_be.view.AppUserCredentialsView;
import com.mobi.ripple_be.view.AppUserView;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.mobi.ripple_be.util.IdentifierType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${ripple.page-size.simple-posts}")
    private Integer SIMPLE_POST_PAGE_SIZE;

    @Value("${ripple.user-data-dir}")
    private String USER_DATA_PATH;

    @Value("${ripple.page-size.followers-list}")
    private Integer FOLLOWERS_LIST_PAGE_SIZE;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserFollowingRepository userFollowingRepository;
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;
    private final PathService pathService;

    public Mono<AppUserCredentialsModel> getUserCredentialsByIdentifier(
            @NotNull final String identifier,
            @NotNull final String password
    ) {
        Mono<AppUserCredentialsView> foundUser = switch (getIdentifierType(identifier)) {
            case EMAIL -> userRepository.getUserCredentialsByEmail(identifier);
            case USERNAME -> userRepository.getUserCredentialsByUsername(identifier);
            case PHONE_NUMBER, UNDEFINED -> Mono.empty();
        };
        return foundUser
                .flatMap(user ->
                        passwordEncoder.matches(password, user.getPassword()) ?
                                Mono.just(user) : Mono.empty()
                )
                .mapNotNull(userCredentialsView ->
                        conversionService.convert(userCredentialsView, AppUserCredentialsModel.class)
                );
    }

    public Mono<Boolean> issueTokenRevocation() {
        return AuthPrincipalProvider.getAuthenticatedUserMono()
                .flatMap(user -> userRepository.issueTokenRevocationForUser(user.getUsername()));
    }

    public Flux<AppUserModel> findUsersLikeUsername(@NotNull final String username) {
        return userRepository.findAppUserViewsByUsername(username)
                .mapNotNull(userView -> conversionService.convert(userView, AppUserModel.class))
                .flatMap(userModel -> isUserFollowed(UUID.fromString(userModel.getId()))
                        .doOnNext(userModel::setFollowed)
                        .thenReturn(userModel)
                );
    }

    public Mono<AppUserModel> getAppUserModelByUsername(@NotNull final String username) {
        return userRepository.getAppUserViewByUsername(username)
                .mapNotNull(userView -> conversionService.convert(userView, AppUserModel.class))
                .flatMap(userModel -> isUserFollowed(UUID.fromString(userModel.getId()))
                        .doOnNext(userModel::setFollowed)
                        .thenReturn(userModel)
                );
    }

    public Mono<AppUserModel> getAppUserModelById(UUID userId) {
        return userRepository.getAppUserViewById(userId)
                .mapNotNull(userView -> conversionService.convert(userView, AppUserModel.class))
                .flatMap(userModel -> isUserFollowed(UUID.fromString(userModel.getId()))
                        .doOnNext(userModel::setFollowed)
                        .thenReturn(userModel)
                );
    }

    public Mono<RegisterStatus> saveUser(@NotNull final AppUserModel userModel) {
        if (!Files.isWritable(Paths.get(USER_DATA_PATH))) {
            log.error("User data path is not writable. Check permissions!");
            return Mono.just(RegisterStatus.INTERNAL_ERROR);
        }
        return userRepository.getUserCredentialsByUsername(userModel.getUsername())
                .map(userView -> RegisterStatus.USERNAME_EXISTS)
                .switchIfEmpty(userRepository.getUserCredentialsByEmail(userModel.getEmail())
                        .map(userView -> RegisterStatus.EMAIL_EXISTS)
                        .switchIfEmpty(
                                userRepository.save(
                                        Objects.requireNonNull(conversionService.convert(userModel, AppUser.class))
                                ).map(savedUser -> RegisterStatus.SUCCESS)
                        )
                ).onErrorReturn(RegisterStatus.INTERNAL_ERROR);
    }

    public Flux<SimplePostModel> getSimpleUserPostsByUsername(@NotNull final String username, @NotNull final int page) {
        return postRepository
                .getSimpleUserPostsByUsername(username, (page * SIMPLE_POST_PAGE_SIZE), SIMPLE_POST_PAGE_SIZE)
                .mapNotNull(simplePostView -> conversionService.convert(simplePostView, SimplePostModel.class))
                .switchIfEmpty(Flux.empty());
    }


    public Mono<Boolean> followOrUnfollowUser(String usernameToFollow) {
        return userRepository.findByUsername(usernameToFollow)
                .flatMap(userToFollow -> AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                        .flatMap(userRepository::findById)
                        .flatMap(user -> userFollowingRepository
                                .findByUserIdAndFollowingId(user.getId(), userToFollow.getId())
                                .flatMap(userFollowing -> {
                                    user.setFollowing(user.getFollowing() - 1);
                                    userToFollow.setFollowers(userToFollow.getFollowers() - 1);
                                    return userRepository.saveAll(List.of(user, userToFollow))
                                            .then(userFollowingRepository.delete(userFollowing))
                                            .thenReturn(true);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    user.setFollowing(user.getFollowing() + 1);
                                    userToFollow.setFollowers(userToFollow.getFollowers() + 1);
                                    return userRepository.saveAll(List.of(user, userToFollow))
                                            .then(userFollowingRepository.save(
                                                    UserFollowing.builder()
                                                            .userId(user.getId())
                                                            .followingId(userToFollow.getId())
                                                            .build()
                                            )).thenReturn(true);
                                }))
                        )
                );
    }

    public Flux<AppUserModel> getFollowers(String username, int page) {
        return userRepository.findAppUserViewByUsername(username)
                .map(AppUserView::getId)
                .flux()
                .flatMap(userId -> userFollowingRepository
                        .findByFollowingId(userId, PageRequest.of(page, FOLLOWERS_LIST_PAGE_SIZE))
                )
                .map(UserFollowing::getUserId)
                .flatMap(userRepository::findUserViewById)
                .mapNotNull(appUserView -> conversionService.convert(appUserView, AppUserModel.class));
    }

    public Flux<AppUserModel> getFollowing(String username, int page) {
        return userRepository.findAppUserViewByUsername(username)
                .map(AppUserView::getId)
                .flux()
                .flatMap(userId -> userFollowingRepository
                        .findByUserId(userId, PageRequest.of(page, FOLLOWERS_LIST_PAGE_SIZE))
                )
                .map(UserFollowing::getFollowingId)
                .flatMap(userRepository::findUserViewById)
                .mapNotNull(appUserView -> conversionService.convert(appUserView, AppUserModel.class));
    }

    public Mono<byte[]> getAppUserProfilePicture(String username) {
        return userRepository.findAppUserViewByUsername(username)
                .map(AppUserView::getId)
                .map(UUID::toString)
                .publishOn(Schedulers.boundedElastic())
                .handle((userId, sink) -> {
                    var profilePictureFile = pathService.getUserProfilePictureFilePath(userId).toFile();
                    if (!profilePictureFile.exists()) return;
                    try (var fileStream = new FileInputStream(profilePictureFile)) {
                        sink.next(fileStream.readAllBytes());
                    } catch (IOException e) {
                        sink.error(new ImageNotFoundException(e.getMessage()));
                    }
                })
                .cast(byte[].class)
                .defaultIfEmpty(new byte[0])
                .doOnError(Throwable::printStackTrace);
    }

    public Mono<Boolean> uploadProfilePicture(Mono<FilePart> image) {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .flatMap(userId -> image.flatMap(filePart -> {
                            var profilePictureFilePath = pathService.getUserProfilePictureFilePath(userId);
                            var profilePictureFile = profilePictureFilePath.toFile();
                            if (profilePictureFile.exists()) {
                                if (!profilePictureFile.delete()) {
                                    return Mono.error(new ProfilePictureDeletionException(
                                            "Deleting pfp for user with id " + userId + " failed"
                                    ));
                                }
                            }
                            return filePart.transferTo(pathService.getUserProfilePictureFilePath(userId));
                        }
                )).thenReturn(true)
                .onErrorReturn(false);
    }

    public Mono<Boolean> deleteProfilePicture() {
        return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                .map(userId -> {
                    var profilePictureFile = pathService.getUserProfilePictureFilePath(userId).toFile();
                    if (!profilePictureFile.exists()) return true;
                    return profilePictureFile.delete();
                });
    }


    public Mono<Boolean> isUserWithUsernameExist(@NotNull final String username) {
        return userRepository.getUserCredentialsByUsername(username)
                .flatMap(u -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<Boolean> isUserWithEmailExist(@NotNull final String email) {
        return userRepository.getUserCredentialsByEmail(email)
                .flatMap(u -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }


    private Mono<Boolean> isUserFollowed(UUID userToFollow) {
        return AuthPrincipalProvider
                .getAuthenticatedUserUUIDMono()
                .flatMap(authUserId -> userFollowingRepository
                        .findByUserIdAndFollowingId(
                                authUserId,
                                userToFollow
                        )
                )
                .map(v -> true)
                .defaultIfEmpty(false);
    }

    private IdentifierType getIdentifierType(@NotNull String identifier) {
        if (identifier.contains(" ")) {
            return UNDEFINED;
        }
        if (isValidEmail(identifier)) {
            return EMAIL;
        }
        if (isValidUsername(identifier)) {
            return USERNAME;
        }
        return UNDEFINED;
    }

    private boolean isValidEmail(@NotNull String identifier) {
        Pattern pattern = Pattern.compile(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                        "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
                Pattern.CASE_INSENSITIVE
        );
        return pattern.matcher(identifier).matches();
    }

    private boolean isValidUsername(@NotNull String identifier) {
        if (identifier.length() > 40) return false;
        if (identifier.charAt(0) == '.' ||
                identifier.charAt(identifier.length() - 1) == '.'
        ) {
            return false;
        } //Cant start or end with a comma

        boolean hasLetter = false;
        boolean isPrevSeparator = false;
        final String str = identifier.toLowerCase();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.' || str.charAt(i) == '_') {
                if (isPrevSeparator) return false;
                isPrevSeparator = true;
                continue;
            }
            if (isLetter(str.charAt(i)) || isDigit(str.charAt(i))) {
                hasLetter = true;
                isPrevSeparator = false;
                continue;
            }
            return false;
        }
        return hasLetter;
    }

    private boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z');
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    @Deprecated
    private void createRequiredUserDirs(String userId) throws RuntimeException {
        try {
            var path = new File(Paths.get(USER_DATA_PATH, userId, "posts").toString());
            if (path.exists()) {
                log.warn("User data path {} already exists!", path.getPath());
                return;
            }
            if (path.mkdirs()) {
                log.info("Successfully created user directory {}/posts", userId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    public enum RegisterStatus {
        USERNAME_EXISTS("Username already exists"),
        EMAIL_EXISTS("Email already exists"),
        INTERNAL_ERROR("Service unavailable"),
        SUCCESS("Registration successful");

        RegisterStatus(final String statusMessage) {
            this.statusMessage = statusMessage;
        }

        private final String statusMessage;

    }
}
