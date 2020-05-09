package com.cosmin.wsgateway.api.errorhandlers;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.cosmin.wsgateway.api.representation.ErrorRepresentation;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRepresentation> handleError(Exception exception) {
        ErrorRepresentation representation = ErrorRepresentation.builder()
                .errorType("InternalServerError")
                .message(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .errors(new ArrayList<>())
                .status(INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();

        return new ResponseEntity<>(representation, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " "
                    + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ErrorRepresentation representation = ErrorRepresentation.builder()
                .errorType("ConstraintViolation")
                .message(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .errors(errors)
                .status(BAD_REQUEST.getReasonPhrase())
                .build();

        return new ResponseEntity<>(representation, BAD_REQUEST);
    }
}
