package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndpointRepresentationTest {
    private final static String JSON = "{ \"path\": \"/my-ws-endpoint\", \"authentication\": { \"mode\": \"basic\", \"username\": \"basic_user\", \"password\": \"strongPassword\" }, \"routes\": [ { \"type\": \"connect\", \"name\": \"Connect\", \"backends\": [ { \"type\": \"http\", \"destination\": \"service.1.example.com/connect\", \"timeoutInMillis\": 300, \"additionalHeaders\": { \"X-Debug-Id\": \"id\" } }, { \"type\": \"kafka\", \"topic\": \"service.2.connect.topic\" } ] }, { \"type\": \"disconnect\", \"name\": \"Disconnect\", \"backends\": [ { \"type\": \"http\", \"destination\": \"service.1.example.com/disconnect\", \"timeoutInMillis\": 300, \"additionalHeaders\": { \"X-Debug-Id\": \"id\" } }, { \"type\": \"kafka\", \"topic\": \"service.2.diconnect.topic\" } ] }, { \"type\": \"default\", \"name\": \"Default\", \"backends\": [ { \"type\": \"http\", \"destination\": \"service.1.example.com/default\", \"timeoutInMillis\": 300 } ] } ] }";

    @Test
    public void testDeserialize() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        EndpointRepresentation representation = objectMapper.readValue(JSON, EndpointRepresentation.class);
        assertEquals("my-ws-endpoint", representation.getPath());
    }
}