package com.cosmin.wsgateway.api.spring.errors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.cosmin.wsgateway.api.representation.ErrorRepresentation;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultControllerAdvice {

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
