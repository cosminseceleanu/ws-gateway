package com.cosmin.wsgateway.infrastructure.gateway.connectors;

public class InvalidStatusCodeException extends RuntimeException {
    public InvalidStatusCodeException(String message) {
        super(message);
    }
}
