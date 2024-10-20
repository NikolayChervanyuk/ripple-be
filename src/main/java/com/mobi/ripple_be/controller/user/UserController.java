package com.mobi.ripple_be.controller.user;

import com.mobi.ripple_be.controller.user.reqrespbody.SimpleUserPostResponse;
import com.mobi.ripple_be.controller.user.reqrespbody.SimpleUserResponse;
import com.mobi.ripple_be.controller.user.reqrespbody.UserProfileResponse;
import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import com.mobi.ripple_be.service.UserService;
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
        return userService.findUsersLikeUsername(like)
                .mapNotNull(userModel -> conversionService.convert(userModel, UserProfileResponse.class))
                .collectList()
                .map(RespModelImpl::of)
                .defaultIfEmpty(RespModelImpl.ofError("No users found username"))
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @GetMapping("user/{username}")
    public Mono<ResponseEntity<RespModelImpl<UserProfileResponse>>> getUserByUsername(
            @PathVariable(name = "username") String username
    ) {
        return userService.getAppUserModelByUsername(username)
                .mapNotNull(userModel -> conversionService.convert(userModel, UserProfileResponse.class))
                .map(userResponse -> ResponseEntity.ok(RespModelImpl.of(userResponse)))
                .defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("No users found"),
                        HttpStatus.NOT_FOUND
                ))
                .onErrorReturn(ResponseEntity.internalServerError().body(RespModelImpl.serviceUnavailableError()));
    }

    @GetMapping(value = "user", params = "username")
    public Mono<ResponseEntity<SimpleUserResponse>> getSimpleUserByUsername(
            @RequestParam("username") String username
    ) {
        return userService.getAppUserModelByUsername(username)
                .mapNotNull(userModel -> conversionService.convert(userModel, SimpleUserResponse.class))
                .map(ResponseEntity::ok);
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
                .defaultIfEmpty(RespModelImpl.of(new ArrayList<>())) //should probably send empty array instead of error?
                .onErrorReturn(RespModelImpl.serviceUnavailableError());
    }

    @GetMapping(value = "user-pfp/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<ResponseEntity<byte[]>> getProfilePicture(@PathVariable String username) {
        return userService.getAppUserProfilePicture(username)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @PostMapping(value = "user-pfp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> uploadProfilePicture(@RequestPart("image") Mono<FilePart> image) {
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
