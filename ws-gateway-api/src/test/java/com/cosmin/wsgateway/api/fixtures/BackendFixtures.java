package com.cosmin.wsgateway.api.fixtures;

import com.cosmin.wsgateway.api.representation.HttpBackendRepresentation;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;

import java.util.Map;

public final class BackendFixtures {

    public static final String HTTP_DESTINATION = "http://localhost:8080/test";
    public static final Map<String, String> ADDITIONAL_HEADERS = Map.of("key", "value");

    private BackendFixtures() {}

    public static HttpBackendRepresentation defaultHttpRepresentation() {
        return HttpBackendRepresentation.builder()
                .destination(HTTP_DESTINATION)
                .timeoutInMillis(120)
                .additionalHeaders(ADDITIONAL_HEADERS)
                .build();
    }

    public static HttpBackend defaultHttpBackend() {
        return HttpBackend.builder()
                .destination(HTTP_DESTINATION)
                .settings(HttpSettings.builder().timeoutInMillis(120).additionalHeaders(ADDITIONAL_HEADERS).build())
                .build();
    }
}
