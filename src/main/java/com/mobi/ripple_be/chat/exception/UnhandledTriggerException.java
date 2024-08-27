package com.mobi.ripple_be.chat.exception;

import com.mobi.ripple_be.exception.ApplicationException;

public class UnhandledTriggerException extends ApplicationException {

    public UnhandledTriggerException(String message) {
        super(message);
    }
}
