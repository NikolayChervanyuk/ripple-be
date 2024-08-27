package com.mobi.ripple_be.chat.repository.mongo;

import com.mobi.ripple_be.chat.entity.mongo.PendingMessageUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PendingMessageUserRepository extends ReactiveMongoRepository<PendingMessageUser, UUID> {

    Flux<PendingMessageUser> findByUserId(String userId);
}
