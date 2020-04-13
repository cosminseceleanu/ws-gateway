package com.cosmin.wsgateway.api.rest.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
public class KafkaBackendRepresentation implements BackendRepresentation {
    private String topic;

    @Override
    @JsonProperty("type")
    public String type() {
        return null;
    }
}
