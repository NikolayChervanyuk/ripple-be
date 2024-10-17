package com.mobi.ripple_be.repository;

import com.mobi.ripple_be.entity.AppUser;
import com.mobi.ripple_be.view.AppUserCredentialsView;
import com.mobi.ripple_be.view.AppUserView;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<AppUser, UUID> {

    Mono<AppUser> getByUsername(String username);

    Mono<AppUserView> getAppUserViewByUsername(String username);

    Mono<AppUserView> getAppUserViewById(UUID userId);

    @Query("SELECT * FROM app_user AS u " +
            "WHERE u.id != :currentUserId AND " +
            "u.username = :username")
    Mono<AppUserView> getOtherAppUserViewByUsername(UUID currentUserId, String username);

    @Query("SELECT * FROM app_user AS u " +
            "WHERE u.id != :currentUserId AND " +
            "u.email = :email")
    Mono<AppUserView> getOtherAppUserViewByEmail(UUID currentUserId, String email);

    @Query("SELECT * FROM app_user AS u WHERE u.username = $1")
    Mono<AppUserCredentialsView> getUserCredentialsByUsername(String username);

    @Query("SELECT * FROM app_user AS u WHERE u.email = $1")
    Mono<AppUserCredentialsView> getUserCredentialsByEmail(String email);

    @Query("SELECT * FROM app_user AS u " +
            "JOIN chat_user AS cu ON u.id = cu.user_id " +
            "WHERE cu.chat_id = :chatId")
    Flux<AppUserView> findAppUserViewsFromChat(UUID chatId);

    @Query("SELECT * " +
            "FROM app_user AS u " +
            "LEFT JOIN user_following AS uf ON uf.user_id = u.id " +
            "WHERE u.username ILIKE :username || '%' " +
            "ORDER BY (uf.following_id IS NOT NULL) DESC , u.followers DESC " +
            "LIMIT 16")
    Flux<AppUserView> findAppUserViewsByUsername(String username);

    @Query("SELECT * " +
            "FROM app_user AS u " +
            "LEFT JOIN user_following AS uf ON uf.user_id = u.id " +
            "WHERE u.full_name ILIKE :fullName || '%' " +
            "ORDER BY (uf.following_id IS NOT NULL) DESC , u.followers DESC " +
            "LIMIT 16")
    Flux<AppUserView> findAppUserViewsByFullName(String fullName);

    @Modifying
    @Query("UPDATE app_user SET last_issued_token_revocation = current_timestamp WHERE username = $1")
    Mono<Boolean> issueTokenRevocationForUser(String username);
}
