package com.cosmin.wsgateway.domain.exceptions;

public class EventTransformationException extends RuntimeException {
    public EventTransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
