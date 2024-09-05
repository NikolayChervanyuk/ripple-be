package com.mobi.ripple_be.configuration;

import com.mobi.ripple_be.exception.EntityNotFoundException;
import com.mobi.ripple_be.model.respmodel.RespModelImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Priority;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@ControllerAdvice
@Priority(0)
public class DefaultWebExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.info("Handling exception: {}", ex.getMessage());


        Mono<ServerResponse> responseMono = switch (ex) {
            case ExpiredJwtException e -> handleExpiredJwtException();
            case EntityNotFoundException e -> handleEntityNotFoundException(e);
            default -> throw new RuntimeException(ex);
        };

        return responseMono.flatMap(response -> response.writeTo(exchange, new ResponseContextInstance())).then();
    }

    private Mono<ServerResponse> handleExpiredJwtException() {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }

    public Mono<ServerResponse> handleEntityNotFoundException(EntityNotFoundException ex) {

        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .bodyValue(RespModelImpl.ofError(ex.getMessage()));
    }

    private static class ResponseContextInstance implements ServerResponse.Context {

        HandlerStrategies strategies = HandlerStrategies.withDefaults();

        @Override
        @NonNull
        public List<HttpMessageWriter<?>> messageWriters() {
            return strategies.messageWriters();
        }

        @Override
        @NonNull
        public List<ViewResolver> viewResolvers() {
            return strategies.viewResolvers();
        }
    }
}
