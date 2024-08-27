package com.mobi.ripple_be.chat.repository.postgres;

import com.mobi.ripple_be.chat.entity.postgres.ChatUser;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChatUserRepository extends R2dbcRepository<ChatUser, UUID> {

    Flux<ChatUser> findAllByChatId(UUID chatId);

    @Modifying
    Mono<Boolean> deleteByChatIdAndUserId(UUID chatId, UUID userId);
}
