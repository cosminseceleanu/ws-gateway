package com.cosmin.wsgateway.application.gateway;

public interface PayloadTransformer {
    <T> String fromPayload(T payload);

    <T> T toPayload(String message, Class<T> expectedType);
}
