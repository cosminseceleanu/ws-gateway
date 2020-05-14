package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorRepresentation {
    private String errorType;
    private Long timestamp;
    private String message;
    private List<String> errors;
    private String status;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private HttpStatus httpStatus;

    public static ErrorRepresentation unknownError(Throwable exception) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError", exception.getMessage());
    }

    public static ErrorRepresentation of(HttpStatus status, String type, String errorMessage) {
        return ErrorRepresentation.builder()
                .errorType(type)
                .message(errorMessage)
                .timestamp(System.currentTimeMillis())
                .errors(new ArrayList<>())
                .status(status.getReasonPhrase())
                .httpStatus(status)
                .build();
    }

    public Mono<ResponseEntity<ErrorRepresentation>> toAsyncResponse() {
        return Mono.just(toResponse());
    }

    public ResponseEntity<ErrorRepresentation> toResponse() {
        return new ResponseEntity<>(this, httpStatus);
    }
}
