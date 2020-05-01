package com.cosmin.wsgateway.tests.client;


import com.cosmin.wsgateway.api.representation.EndpointRepresentation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class EndpointsClient {
    private final WebTestClient webTestClient;

    public EndpointRepresentation createAndAssert(EndpointRepresentation initial) {
        return create(initial)
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(EndpointRepresentation.class)
                .returnResult()
                .getResponseBody();
    }

    public WebTestClient.ResponseSpec create(EndpointRepresentation initial) {
        return this.webTestClient.post()
                .uri(Api.Paths.ENDPOINTS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(initial), EndpointRepresentation.class)
                .exchange();
    }
}
