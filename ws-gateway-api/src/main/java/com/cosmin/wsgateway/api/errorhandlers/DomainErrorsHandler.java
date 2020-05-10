package com.cosmin.wsgateway.api.errorhandlers;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.cosmin.wsgateway.api.representation.ErrorRepresentation;
import com.cosmin.wsgateway.domain.exceptions.BackendNotSupportedException;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import com.cosmin.wsgateway.domain.exceptions.IncorrectExpressionException;
import com.cosmin.wsgateway.domain.exceptions.InvalidRouteException;
import com.cosmin.wsgateway.domain.exceptions.RouteNotFoundException;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainErrorsHandler {
    @ExceptionHandler(EndpointNotFoundException.class)
    public ResponseEntity<ErrorRepresentation> handleEndpointNotFound(EndpointNotFoundException exception) {
        ErrorRepresentation representation = ErrorRepresentation.builder()
                .errorType("EndpointNotFound")
                .message(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .errors(new ArrayList<>())
                .status(NOT_FOUND.getReasonPhrase())
                .build();

        return new ResponseEntity<>(representation, NOT_FOUND);
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorRepresentation> handleRouteNotFound(RouteNotFoundException exception) {
        ErrorRepresentation representation = ErrorRepresentation.builder()
                .errorType("RouteNotFound")
                .message(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .errors(new ArrayList<>())
                .status(NOT_FOUND.getReasonPhrase())
                .build();

        return new ResponseEntity<>(representation, NOT_FOUND);
    }

    @ExceptionHandler(BackendNotSupportedException.class)
    public ResponseEntity<ErrorRepresentation> handleBackendNotSupported(BackendNotSupportedException exception) {
        ErrorRepresentation representation = ErrorRepresentation.builder()
                .errorType("BackendNotSupported")
                .message(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .errors(new ArrayList<>())
                .status(BAD_REQUEST.getReasonPhrase())
                .build();

        return new ResponseEntity<>(representation, BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectExpressionException.class)
    public ResponseEntity<ErrorRepresentation> handleIncorrectExpression(IncorrectExpressionException exception) {
        ErrorRepresentation representation = ErrorRepresentation.builder()
                .errorType("IncorrectExpression")
                .message(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .errors(new ArrayList<>())
                .status(BAD_REQUEST.getReasonPhrase())
                .build();

        return new ResponseEntity<>(representation, BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRouteException.class)
    public ResponseEntity<ErrorRepresentation> handleInvalidRoute(InvalidRouteException exception) {
        ErrorRepresentation representation = ErrorRepresentation.builder()
                .errorType("InvalidRoute")
                .message(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .errors(new ArrayList<>())
                .status(BAD_REQUEST.getReasonPhrase())
                .build();

        return new ResponseEntity<>(representation, BAD_REQUEST);
    }
}
