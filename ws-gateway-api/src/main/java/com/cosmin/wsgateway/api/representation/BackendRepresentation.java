package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HttpBackendRepresentation.class, name = "http"),
        @JsonSubTypes.Type(value = KafkaBackendRepresentation.class, name = "kafka"),
})
public interface BackendRepresentation {
    String type();
}
