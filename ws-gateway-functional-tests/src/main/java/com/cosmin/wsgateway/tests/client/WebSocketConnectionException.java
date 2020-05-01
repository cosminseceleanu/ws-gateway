package com.cosmin.wsgateway.tests.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WebSocketConnectionException extends RuntimeException {
    private final HttpStatus httpStatus;

    public WebSocketConnectionException(HttpStatus httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }
}
