package com.cosmin.wsgateway.api.spring.errors;


import com.cosmin.wsgateway.api.representation.ErrorRepresentation;
import com.cosmin.wsgateway.api.utils.HttpLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@Order(-2)
@Slf4j
public class ReactiveErrorHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        return findErrorResponse(throwable)
                .flatMap(entity -> setHttpResponse(serverWebExchange, entity));
    }

    private Mono<? extends Void> setHttpResponse(
            ServerWebExchange serverWebExchange,
            ResponseEntity<ErrorRepresentation> entity
    ) {
        final ServerHttpResponse response = serverWebExchange.getResponse();
        response.setStatusCode(entity.getStatusCode());
        response.getHeaders().addAll(entity.getHeaders());
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            final DataBuffer buffer = response.bufferFactory()
                    .wrap(objectMapper.writeValueAsBytes(entity.getBody()));
            return response.writeWith(Mono.just(buffer)
                    .map(b -> HttpLogger.logResponse(b, response, serverWebExchange.getRequest())))
                    .doOnError(error -> DataBufferUtils.release(buffer));
        } catch (final JsonProcessingException ex) {
            return Mono.error(ex);
        }
    }

    private Mono<ResponseEntity<ErrorRepresentation>> findErrorResponse(Throwable throwable) {
        if (throwable instanceof NotAcceptableStatusException) {
            return createResponse(HttpStatus.NOT_ACCEPTABLE, "NotAcceptable", throwable.getMessage());
        } else if (throwable instanceof UnsupportedMediaTypeStatusException) {
            return createResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UnsupportedMediaType", throwable.getMessage());
        } else if (throwable instanceof MethodNotAllowedException) {
            return createResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UnsupportedMediaType", throwable.getMessage());
        } else if (throwable instanceof ResponseStatusException) {
            var responseStatusException = (ResponseStatusException) throwable;
            return createResponse(
                    responseStatusException.getStatus(),
                    responseStatusException.getReason(),
                    responseStatusException.getMessage());
        }
        return ErrorRepresentation.unknownError(throwable).toAsyncResponse();
    }

    private Mono<ResponseEntity<ErrorRepresentation>> createResponse(HttpStatus status, String type, String message) {
        return ErrorRepresentation.of(status, type, message).toAsyncResponse();
    }

}
