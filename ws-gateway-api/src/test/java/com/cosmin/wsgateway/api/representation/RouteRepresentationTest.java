package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class RouteRepresentationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSerialize() {
        RouteRepresentation representation = RouteRepresentation.builder()
                .backends(Collections.emptySet())
                .name("Connect")
                .type(RouteRepresentation.Type.CONNECT)
                .build();
    }

}
