package com.mobi.ripple_be.chat.repository.mongo;

import com.mobi.ripple_be.chat.entity.mongo.PendingMessagesUsers;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PendingMessagesUsersRepository extends ReactiveMongoRepository<PendingMessagesUsers, UUID> {

    Flux<PendingMessagesUsers> findByUserId(String userId);


}
