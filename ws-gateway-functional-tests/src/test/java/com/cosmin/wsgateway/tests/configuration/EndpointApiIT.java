package com.cosmin.wsgateway.tests.configuration;

import com.cosmin.wsgateway.api.representation.EndpointRepresentation;
import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.client.Api;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EndpointApiIT extends BaseTestIT {
    @Test
    public void createEndpoint() {
        var initial = EndpointFixtures.defaultRepresentation();

        var created = this.webClient.post()
                .uri(Api.Paths.ENDPOINTS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(initial), EndpointRepresentation.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(EndpointRepresentation.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created.getId());
        assertEquals(initial.getAuthentication(), created.getAuthentication());
        assertEquals(initial.getPath(), created.getPath());
        assertEquals(initial.getRoutes(), created.getRoutes());
    }
}
