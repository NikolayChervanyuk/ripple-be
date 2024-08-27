package com.mobi.ripple_be.chat.repository.mongo;

import com.mobi.ripple_be.chat.entity.mongo.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

    Flux<Message> findByMsgIdOrderBySentDateAsc(String msgId);//TODO: use this
}
