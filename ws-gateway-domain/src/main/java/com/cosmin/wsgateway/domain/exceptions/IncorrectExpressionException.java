package com.cosmin.wsgateway.domain.exceptions;

public class IncorrectExpressionException extends RuntimeException {
    public IncorrectExpressionException(String message) {
        super(message);
    }

    public IncorrectExpressionException(Throwable cause) {
        super(cause);
    }
}
