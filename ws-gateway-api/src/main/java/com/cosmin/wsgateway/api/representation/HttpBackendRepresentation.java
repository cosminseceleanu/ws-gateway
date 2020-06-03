package com.cosmin.wsgateway.api.representation;

import com.cosmin.wsgateway.domain.Backend;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpBackendRepresentation implements BackendRepresentation {
    private static Integer DEFAULT_READ_TIMEOUT = 1500;
    private static Integer DEFAULT_CONNECT_TIMEOUT = 200;

    private String destination;

    @Builder.Default
    private Integer readTimeoutInMillis = DEFAULT_READ_TIMEOUT;

    @Builder.Default
    private Integer connectTimeoutInMillis = DEFAULT_CONNECT_TIMEOUT;

    @Builder.Default
    private Map<String, String> additionalHeaders = new HashMap<>();

    @JsonProperty("type")
    @Override
    public String type() {
        return Backend.Type.HTTP.name().toLowerCase();
    }
}
