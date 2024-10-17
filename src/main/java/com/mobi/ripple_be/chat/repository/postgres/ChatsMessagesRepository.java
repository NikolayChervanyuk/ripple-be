package com.mobi.ripple_be.chat.repository.postgres;

import com.mobi.ripple_be.chat.entity.postgres.ChatsMessages;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ChatsMessagesRepository extends R2dbcRepository<ChatsMessages, UUID> {

    @Query("SELECT * FROM chats_messages " +
            "WHERE chat_id = :chatId " +
            "ORDER BY creation_date DESC " +
            "LIMIT :pageSize " +
            "OFFSET :offset")
    Flux<ChatsMessages> findByChatId(UUID chatId, int pageSize, long offset);
}
