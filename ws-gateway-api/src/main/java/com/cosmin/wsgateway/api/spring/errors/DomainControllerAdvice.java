package com.cosmin.wsgateway.api.spring.errors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.cosmin.wsgateway.api.representation.ErrorRepresentation;
import com.cosmin.wsgateway.domain.exceptions.BackendNotSupportedException;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import com.cosmin.wsgateway.domain.exceptions.IncorrectExpressionException;
import com.cosmin.wsgateway.domain.exceptions.InvalidRouteException;
import com.cosmin.wsgateway.domain.exceptions.RouteNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainControllerAdvice {
    @ExceptionHandler(EndpointNotFoundException.class)
    public ResponseEntity<ErrorRepresentation> handleEndpointNotFound(EndpointNotFoundException exception) {
        return ErrorRepresentation.of(NOT_FOUND, "EndpointNotFound", exception.getMessage()).toResponse();
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorRepresentation> handleRouteNotFound(RouteNotFoundException exception) {
        return ErrorRepresentation.of(NOT_FOUND, "RouteNotFound", exception.getMessage()).toResponse();
    }

    @ExceptionHandler(BackendNotSupportedException.class)
    public ResponseEntity<ErrorRepresentation> handleBackendNotSupported(BackendNotSupportedException exception) {
        return ErrorRepresentation.of(BAD_REQUEST, "BackendNotSupported", exception.getMessage()).toResponse();
    }

    @ExceptionHandler(IncorrectExpressionException.class)
    public ResponseEntity<ErrorRepresentation> handleIncorrectExpression(IncorrectExpressionException exception) {
        return ErrorRepresentation.of(BAD_REQUEST, "IncorrectExpression", exception.getMessage()).toResponse();
    }

    @ExceptionHandler(InvalidRouteException.class)
    public ResponseEntity<ErrorRepresentation> handleInvalidRoute(InvalidRouteException exception) {
        return ErrorRepresentation.of(BAD_REQUEST, "InvalidRoute", exception.getMessage()).toResponse();
    }
}
