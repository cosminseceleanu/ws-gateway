package com.cosmin.infrastructure.gateway;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.domain.exceptions.EventTransformationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JacksonMessageTransformer implements PayloadTransformer {
    private final ObjectMapper objectMapper;

    @Override
    public <T> String fromPayload(T payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new EventTransformationException("Couldn't transform event payload to JSON", e);
        }
    }

    @Override
    public <T> T toPayload(String message, Class<T> expectedType) {
        try {
            return objectMapper.readValue(message, expectedType);
        } catch (JsonProcessingException e) {
            throw new EventTransformationException("Couldn't transform JSON to event payload", e);
        }
    }
}
