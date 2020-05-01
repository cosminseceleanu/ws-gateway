package com.cosmin.wsgateway.domain.exceptions;

public class BackendNotSupportedException extends RuntimeException {
    public BackendNotSupportedException(String type) {
        super(String.format("Backend of type %s is not supported", type));
    }
}
