package com.cosmin.wsgateway.domain.exceptions;

public class EndpointNotFoundException extends RuntimeException {
    public EndpointNotFoundException(String identifier) {
        super("Endpoint not found endpoint=" + identifier);
    }
}
