package com.mobi.ripple_be.chat.repository.postgres;

import com.mobi.ripple_be.chat.entity.postgres.Chat;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends R2dbcRepository<Chat, UUID> {
}
