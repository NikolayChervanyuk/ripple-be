package com.mobi.ripple_be.chat.repository.postgres;

import com.mobi.ripple_be.chat.entity.postgres.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChatRepository extends R2dbcRepository<Chat, UUID> {

    Mono<Chat> findChatById(UUID chatId);

    @Query("SELECT * FROM chat AS c " +
            "JOIN chat_user AS cu ON c.id = cu.chat_id " +
            "WHERE cu.user_id = :userId " +
            "ORDER BY last_sent_time DESC " +
            "OFFSET :offset " +
            "LIMIT :pageSize")
    Flux<Chat> getUserChatsPage(UUID userId, int offset, long pageSize);
}
