package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.cosmin.wsgateway.api.fixtures.BackendFixtures.defaultHttpRepresentation;
import static org.junit.jupiter.api.Assertions.*;

class HttpBackendRepresentationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String json = "{\"destination\":\"http://localhost:8080/test\",\"timeoutInMillis\":120,\"additionalHeaders\":{\"key\":\"value\"},\"type\":\"http\"}";

    @Test
    public void testSerialize() throws JsonProcessingException {
        HttpBackendRepresentation representation = defaultHttpRepresentation();
        String result = objectMapper.writeValueAsString(representation);

        assertEquals(json, result);
    }

    @Test
    public void testDeserialize() throws JsonProcessingException {
        HttpBackendRepresentation expected = defaultHttpRepresentation();
        BackendRepresentation result = objectMapper.readValue(json, BackendRepresentation.class);

        assertEquals(expected, result);
    }
}
