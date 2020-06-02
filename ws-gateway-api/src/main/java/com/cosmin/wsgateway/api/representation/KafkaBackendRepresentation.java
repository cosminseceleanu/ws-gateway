package com.cosmin.wsgateway.api.representation;

import static java.util.stream.Collectors.toMap;

import com.cosmin.wsgateway.api.exception.InvalidRequestException;
import com.cosmin.wsgateway.domain.Backend;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaBackendRepresentation implements BackendRepresentation {
    private String topic;
    private String bootstrapServers;

    @Builder.Default
    private Ack acks = Ack.LEADER;

    @Builder.Default
    private Integer retriesNr = 1;

    @Override
    @JsonProperty("type")
    public String type() {
        return Backend.Type.KAFKA.name().toLowerCase();
    }

    @RequiredArgsConstructor
    public enum Ack {
        NO_ACK("0"),
        LEADER("1"),
        ALL("all");

        private final String value;

        private static final Map<String, Ack> valuesByName = Arrays.stream(Ack.values())
                .collect(toMap(Ack::getValue, Function.identity()));

        @JsonCreator
        public Ack fromValue(String value) {
            return Optional.ofNullable(valuesByName.get(value))
                    .orElseThrow(() -> new InvalidRequestException(
                            String.format("Kafka ack=%s is not supported", value),
                            "InvalidKafkaConfiguration"
                    ));
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
