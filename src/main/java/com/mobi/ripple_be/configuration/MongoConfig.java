package com.mobi.ripple_be.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.mobi.ripple_be.chat.repository.mongo")
public class MongoConfig  {

    @Value("${spring.data.mongodb.database}")
    private String dbName;

//    @Override
//    @NonNull
//    public MongoClient reactiveMongoClient() {
//        return MongoClients.create(
//                MongoClientSettings
//                        .builder()
//                        .credential(MongoCredential
//                                .createCredential(
//                                        "user",
//                                        "chat-db",
//                                        "password".toCharArray())
//                        )
//                        .uuidRepresentation(UuidRepresentation.STANDARD)
//                        .build()
//        );
//    }
//
//    @Override
//    @NonNull
//    protected String getDatabaseName() {
//        return dbName;
//    }
//
//    @Bean
//    public ReactiveMongoTemplate reactiveMongoTemplate() {
//        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
//    }
}
