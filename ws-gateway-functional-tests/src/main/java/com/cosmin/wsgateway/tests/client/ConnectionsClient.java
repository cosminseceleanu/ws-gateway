package com.cosmin.wsgateway.tests.client;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ConnectionsClient {
    private final WebTestClient webTestClient;

    public void sendOutbound(String connectionId, String payload) {
        this.webTestClient.post()
                .uri(Api.Paths.CONNECTIONS + "/" + connectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), String.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.ACCEPTED);
    }
}
