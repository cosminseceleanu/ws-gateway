package com.cosmin.wsgateway.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class EndpointTest {

    @Test
    public void immutable_whenRoutesAreChanged_thenExceptionIsThrown() {
        Endpoint endpoint = Endpoint.builder()
                .configuration(EndpointConfiguration.builder().routes(new HashSet<>()).build())
                .build();

        assertThrows(UnsupportedOperationException.class, () -> {
            endpoint.getRoutes().add(Route.connect());
        });
    }
}
