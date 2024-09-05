package com.mobi.ripple_be.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "App exception thrown!";

    public ApplicationException(String message) {
        super(DEFAULT_MESSAGE + " -> " + message);
        log.error(DEFAULT_MESSAGE + ": {}", message);
    }

    public ApplicationException() {
        this("No further information given");
    }
}
