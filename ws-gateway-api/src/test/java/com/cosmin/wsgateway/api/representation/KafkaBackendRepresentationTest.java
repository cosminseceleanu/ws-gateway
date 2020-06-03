package com.cosmin.wsgateway.api.representation;

import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.api.fixtures.BackendFixtures;
import com.cosmin.wsgateway.api.utils.JsonUtils;
import org.junit.jupiter.api.Test;

class KafkaBackendRepresentationTest {
    private final static String JSON = "{\"topic\":\"topic\",\"bootstrapServers\":\"servers\",\"acks\":\"1\",\"retriesNr\":1,\"type\":\"kafka\"}";
    private final static String INVALID_JSON = "{\"topic\":\"topic\",\"bootstrapServers\":\"servers\",\"acks\":\"adasdasd\",\"retriesNr\":1,\"type\":\"kafka\"}";

    @Test
    public void testDeserialize() {
        var expected = BackendFixtures.defaultKafkaRepresentation();
        var result = JsonUtils.fromJson(JSON, KafkaBackendRepresentation.class);
        assertEquals(expected, result);
    }

    @Test
    public void testDeserialize_whenAckIsNotSupported_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            JsonUtils.fromJson(INVALID_JSON, KafkaBackendRepresentation.class);
        });
    }

    @Test
    public void testSerialize() {
        var subject = BackendFixtures.defaultKafkaRepresentation();
        String result = JsonUtils.toJson(subject);
        assertEquals(JSON, result);
    }
}