package com.cosmin.wsgateway.api.representation;

import com.cosmin.wsgateway.domain.Backend;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaBackendRepresentation implements BackendRepresentation {
    private String topic;
    private String bootstrapServers;

    @Override
    @JsonProperty("type")
    public String type() {
        return Backend.Type.KAFKA.name().toLowerCase();
    }
}
