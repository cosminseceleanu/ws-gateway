package com.cosmin.wsgateway.api.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {
    private String errorType;

    public InvalidRequestException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }
}
