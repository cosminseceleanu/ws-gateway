package com.cosmin.wsgateway.domain.exceptions;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String message) {
        super(message);
    }

    public RouteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
