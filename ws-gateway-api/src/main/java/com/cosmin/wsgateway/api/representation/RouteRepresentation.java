package com.cosmin.wsgateway.api.representation;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteRepresentation {

    private Type type;
    private String name;
    private Set<BackendRepresentation> backends;
    private Map<String, Object> expression;

    public enum Type {
        CONNECT("connect"),
        CUSTOM("custom"),
        DISCONNECT("disconnect"),
        DEFAULT("default");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        private static final Map<String, RouteRepresentation.Type> valuesByName = Arrays.stream(Type.values())
                .collect(toMap(Type::getValue, Function.identity()));

        @JsonCreator
        public RouteRepresentation.Type fromValue(String value) {
            return Optional.ofNullable(valuesByName.get(value))
                    .orElseThrow(() -> new RuntimeException("Route type not supported"));
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
