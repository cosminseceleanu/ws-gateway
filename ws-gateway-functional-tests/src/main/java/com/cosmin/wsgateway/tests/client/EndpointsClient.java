package com.cosmin.wsgateway.tests.client;


import com.cosmin.wsgateway.infrastructure.representation.EndpointRepresentation;
import java.util.List;
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

    public List<EndpointRepresentation> getAllAndAssert() {
        return this.webTestClient.get()
                .uri(Api.Paths.ENDPOINTS)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBodyList(EndpointRepresentation.class)
                .returnResult()
                .getResponseBody();
    }

    public WebTestClient.ResponseSpec getById(String id) {
        return this.webTestClient.get()
                .uri(Api.Paths.ENDPOINTS + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    public void deleteAndAssert(String id) {
        delete(id).expectStatus().isEqualTo(HttpStatus.ACCEPTED);
    }

    public WebTestClient.ResponseSpec delete(String id) {
        return this.webTestClient.delete()
                .uri(Api.Paths.ENDPOINTS + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    public EndpointRepresentation updateAndAssert(String id, EndpointRepresentation representation) {
        return update(id, representation)
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(EndpointRepresentation.class)
                .returnResult()
                .getResponseBody();
    }

    public WebTestClient.ResponseSpec update(String id, EndpointRepresentation representation) {
        return this.webTestClient.put()
                .uri(Api.Paths.ENDPOINTS + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(representation), EndpointRepresentation.class)
                .exchange();
    }
}
