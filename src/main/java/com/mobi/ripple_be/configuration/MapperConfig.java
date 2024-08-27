package com.mobi.ripple_be.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mobi.ripple_be.chat.websocket.util.ChatObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule timeModule = new JavaTimeModule();
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(timeModule);

        return objectMapper;
    }

    @Bean
    public ChatObjectMapper chatObjectMapper() {

        final var objectMapper = new ChatObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        JavaTimeModule timeModule = new JavaTimeModule();
        objectMapper.registerModule(timeModule);

        return objectMapper;
    }
}
