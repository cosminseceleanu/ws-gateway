package com.cosmin.wsgateway.api;

import com.cosmin.wsgateway.api.rest.representation.HttpBackendRepresentation;

import java.util.Map;

public class BackendFixtures {
    private BackendFixtures() {}

    public static HttpBackendRepresentation defaultHttpRepresentation() {
        return HttpBackendRepresentation.builder()
                .destination("http://localhost:8080/test")
                .timeoutInMillis(120)
                .additionalHeaders(Map.of("key", "value"))
                .build();
    }
}
