package com.cosmin.wsgateway.api.representation;

import com.cosmin.wsgateway.domain.model.Backend;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class HttpBackendRepresentation implements BackendRepresentation {
    private String destination;
    private Integer timeoutInMillis;
    private Map<String, String> additionalHeaders;

    @JsonProperty("type")
    @Override
    public String type() {
        return Backend.Type.HTTP.name().toLowerCase();
    }
}
