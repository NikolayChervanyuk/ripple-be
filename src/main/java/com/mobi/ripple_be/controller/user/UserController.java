package com.mobi.ripple_be.controller.user;

import com.mobi.ripple_be.controller.user.reqrespbody.SimpleUserPostResponse;
import com.mobi.ripple_be.controller.user.reqrespbody.SimpleUserResponse;
import com.mobi.ripple_be.controller.user.reqrespbody.UpdateUserRequest;
import com.mobi.ripple_be.controller.user.reqrespbody.UserProfileResponse;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import com.mobi.ripple_be.service.UserService;
import com.mobi.ripple_be.util.AuthPrincipalProvider;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final ConversionService conversionService;

    @GetMapping(value = "users", params = "username")
    public Mono<RespModelImpl<Boolean>> isUserExistWithUsername(@RequestParam String username) {
        return userService.isUserWithUsernameExist(username)
                .map(RespModelImpl::of);
    }

    @GetMapping(value = "users", params = "email")
    public Mono<RespModelImpl<Boolean>> isUserExistWithEmail(@RequestParam String email) {
        return userService.isUserWithEmailExist(email)
                .map(RespModelImpl::of);
    }

    @GetMapping(value = "users/find", params = "like")
    public Mono<RespModelImpl<List<UserProfileResponse>>> findUsersLikeUsername(@RequestParam String like) {
        return userService.findUsersLike(null, like)
                .mapNotNull(userModel -> conversionService.convert(userModel, UserProfileResponse.class))
                .collectList()
                .map(RespModelImpl::of)
                .defaultIfEmpty(RespModelImpl.ofError("No users of such username"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @GetMapping(value = "users/find-simple")
    public Mono<ResponseEntity<RespModelImpl<List<SimpleUserResponse>>>> findSimpleUsersLike(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "fullName", required = false) String fullName
    ) {
        if (username == null && fullName == null) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(RespModelImpl.ofError("Username or Full Name is required")));
        }
        return userService.findUsersLike(fullName, username)
                .mapNotNull(userModel -> conversionService.convert(userModel, SimpleUserResponse.class))
                .collectList()
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(
                        new ResponseEntity<>(
                                RespModelImpl.ofError("No users found with provided query parameters"),
                                HttpStatus.NOT_FOUND
                        )
                )
                .onErrorReturn(
                        new ResponseEntity<>(
                                RespModelImpl.serviceUnavailableError(),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );
    }

    @GetMapping("user/{username}")
    public Mono<ResponseEntity<RespModelImpl<UserProfileResponse>>> getUserByUsername(
            @PathVariable(name = "username") String username
    ) {
        return userService.getAppUserModelByUsername(username)
                .flatMap(userModel -> {
                    var userProfileResponse = Objects.requireNonNull(
                            conversionService.convert(userModel, UserProfileResponse.class));
                    return AuthPrincipalProvider.getAuthenticatedUserIdMono()
                            .map(userId -> {
                                if (userId.equals(userModel.getId())) {
                                    userProfileResponse.setEmail(userModel.getEmail());
                                }
                                return userProfileResponse;
                            });
                })
                .map(userResponse -> ResponseEntity.ok(RespModelImpl.of(userResponse)))
                .defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("No user found"),
                        HttpStatus.NOT_FOUND
                ))
                .onErrorReturn(ResponseEntity.internalServerError().body(RespModelImpl.serviceUnavailableError()));
    }

    @GetMapping(value = "users/other", params = "username")
    public Mono<RespModelImpl<Boolean>> isOtherUserExistWithUsername(@RequestParam String username) {
        return userService.isOtherUserWithUsernameExist(username)
                .map(RespModelImpl::of);
    }

    @GetMapping(value = "users/other", params = "email")
    public Mono<RespModelImpl<Boolean>> isOtherUserExistWithEmail(@RequestParam String email) {
        return userService.isOtherUserWithEmailExist(email)
                .map(RespModelImpl::of);
    }

    @PutMapping("user")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> updateUser(
            @RequestBody UpdateUserRequest updateUserRequest
    ) {
        return userService.updateUser(Objects.requireNonNull(
                conversionService.convert(updateUserRequest, AppUserModel.class))
        ).map(persistenceStatus ->
                switch (persistenceStatus) {
                    case SUCCESS -> ResponseEntity.ok(RespModelImpl.of(true));
                    case USERNAME_EXISTS, EMAIL_EXISTS -> new ResponseEntity<>(
                            RespModelImpl.ofError(persistenceStatus.getStatusMessage()),
                            HttpStatus.CONFLICT
                    );
                    case INTERNAL_ERROR -> new ResponseEntity<>(
                            RespModelImpl.ofError(persistenceStatus.getStatusMessage()),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    );
                });
    }

    @GetMapping(value = "user", params = "username")
    public Mono<ResponseEntity<RespModelImpl<SimpleUserResponse>>> getSimpleUserByUsername(
            @RequestParam("username") String username
    ) {
        return userService.getAppUserModelByUsername(username)
                .mapNotNull(userModel -> conversionService.convert(userModel, SimpleUserResponse.class))
                .map(respModel -> ResponseEntity.ok(RespModelImpl.of(respModel)));
    }

    @GetMapping(value = "user", params = "id")
    public Mono<ResponseEntity<RespModelImpl<SimpleUserResponse>>> getSimpleUserById(@RequestParam("id") UUID userId) {
        return userService.getAppUserModelById(userId)
                .mapNotNull(userModel -> conversionService.convert(userModel, SimpleUserResponse.class))
                .map(simpleUserResponse -> ResponseEntity.ok(RespModelImpl.of(simpleUserResponse)))
                .defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("No user found"),
                        HttpStatus.NOT_FOUND
                ))
                .onErrorReturn(new ResponseEntity<>(
                        RespModelImpl.serviceUnavailableError(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }

    @GetMapping(value = "user/{username}/p", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RespModelImpl<List<SimpleUserPostResponse>>> getLatestSimpleUserPostsWithUsername(
            @PathVariable(name = "username") String username,
            @RequestParam int page
    ) {
        return userService.getSimpleUserPostsByUsername(username, page)
                .mapNotNull(postModel -> conversionService.convert(postModel, SimpleUserPostResponse.class))
                .collectList()
                .map(RespModelImpl::of)
                .defaultIfEmpty(RespModelImpl.ofError("No more users to show"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @PutMapping("user/{username}/follow")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> followOrUnfollowUser(@PathVariable String username) {
        return userService.followOrUnfollowUser(username)
                .map(result -> ResponseEntity.ok().body(RespModelImpl.of(result)))
                .defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("User not found"),
                        HttpStatus.NOT_FOUND
                ))
                .onErrorReturn(ResponseEntity.internalServerError().body(RespModelImpl.serviceUnavailableError()));
    }

    @GetMapping("user/{username}/followers")
    public Mono<RespModelImpl<List<SimpleUserResponse>>> getFollowers(
            @PathVariable String username,
            @RequestParam int page
    ) {
        return userService.getFollowers(username, page)
                .mapNotNull(userModel -> conversionService.convert(userModel, SimpleUserResponse.class))
                .collectList()
                .map(RespModelImpl::of)
                .defaultIfEmpty(RespModelImpl.ofError("No more followers to show"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @GetMapping("user/{username}/following")
    public Mono<RespModelImpl<List<SimpleUserResponse>>> getFollowing(
            @PathVariable String username,
            @RequestParam int page
    ) {
        return userService.getFollowing(username, page)
                .mapNotNull(userModel -> conversionService.convert(userModel, SimpleUserResponse.class))
                .collectList()
                .map(RespModelImpl::of)
                .defaultIfEmpty(RespModelImpl.of(new ArrayList<>()))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @GetMapping(value = "user-pfp/{username}")
    public Mono<ResponseEntity<?>> getProfilePicture(@PathVariable String username) {
        return userService.getAppUserProfilePicture(username)
                .map(pfp -> {
                    if (pfp.length == 0) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(RespModelImpl.of(pfp));
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @PostMapping(value = "user-pfp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> uploadProfilePicture(
            @RequestPart("image") Mono<FilePart> image
    ) {
        return userService.uploadProfilePicture(image)
                .map(pfp -> ResponseEntity.ok(RespModelImpl.of(true)))
                .onErrorReturn(ResponseEntity.internalServerError().body(RespModelImpl.serviceUnavailableError()));
    }

    @DeleteMapping("user-pfp")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> deleteProfilePicture() {
        return userService.deleteProfilePicture()
                .map(pfp -> ResponseEntity.ok(RespModelImpl.of(true)))
                .onErrorReturn(ResponseEntity.internalServerError().body(RespModelImpl.serviceUnavailableError()));
    }
}
