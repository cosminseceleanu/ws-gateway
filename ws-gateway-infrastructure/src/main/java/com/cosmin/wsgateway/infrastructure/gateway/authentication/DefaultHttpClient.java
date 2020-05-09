package com.cosmin.wsgateway.infrastructure.gateway.authentication;

import com.cosmin.wsgateway.application.gateway.authentication.HttpClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class DefaultHttpClient implements HttpClient {
    private final WebClient webClient;

    @Override
    public Mono<Integer> checkToken(String url, String token) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new CheckTokenRequest(token)), CheckTokenRequest.class)
                .retrieve()
                .toEntity(String.class)
                .map(ResponseEntity::getStatusCodeValue);
    }

    @Data
    @AllArgsConstructor
    private static class CheckTokenRequest {
        @JsonProperty("access_token")
        private String accessToken;
    }
}
