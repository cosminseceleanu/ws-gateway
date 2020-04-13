package com.cosmin.wsgateway.api.rest.representation;

import com.cosmin.wsgateway.domain.model.Expression;
import com.cosmin.wsgateway.domain.model.expressions.Equal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

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
