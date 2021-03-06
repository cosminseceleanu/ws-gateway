package com.cosmin.wsgateway.api.representation;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "mode",
        defaultImpl = AuthenticationRepresentation.None.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthenticationRepresentation.None.class, name = "none"),
        @JsonSubTypes.Type(value = AuthenticationRepresentation.Basic.class, name = "basic"),
        @JsonSubTypes.Type(value = AuthenticationRepresentation.Bearer.class, name = "bearer")
})
public interface AuthenticationRepresentation {

    Mode mode();

    @RequiredArgsConstructor
    enum Mode {
        NONE("none"),
        BASIC("basic"),
        BEARER("bearer");

        private final String value;

        private static final Map<String, Mode> valuesByName = Arrays.stream(Mode.values())
                .collect(toMap(Mode::getValue, Function.identity()));

        @JsonCreator
        public Mode fromValue(String value) {
            return Optional.ofNullable(valuesByName.get(value))
                    .orElseThrow(() -> new RuntimeException("Auth mode not defined"));
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    @Data
    @NoArgsConstructor
    class None implements AuthenticationRepresentation {
        @JsonProperty("mode")
        @Override
        public Mode mode() {
            return Mode.NONE;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Basic implements AuthenticationRepresentation {
        private String username;
        private String password;

        @JsonProperty("mode")
        @Override
        public Mode mode() {
            return Mode.BASIC;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Bearer implements AuthenticationRepresentation {
        private String authorizationServerUrl;

        @JsonProperty("mode")
        @Override
        public Mode mode() {
            return Mode.BEARER;
        }
    }
}
