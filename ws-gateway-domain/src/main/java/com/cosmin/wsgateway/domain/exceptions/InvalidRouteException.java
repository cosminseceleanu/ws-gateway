package com.cosmin.wsgateway.domain.exceptions;

public class InvalidRouteException extends RuntimeException {
    public InvalidRouteException(String message) {
        super(message);
    }
}
