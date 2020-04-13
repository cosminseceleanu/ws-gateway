package com.cosmin.wsgateway.api.rest.representation;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

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

        private final Map<String, Mode> valuesByName = Arrays.stream(Mode.values())
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
    class None implements AuthenticationRepresentation {
        @JsonProperty("mode")
        @Override
        public Mode mode() {
            return Mode.NONE;
        }
    };

    @Data
    class Basic implements AuthenticationRepresentation {
        private String username;
        private String password;

        @JsonProperty("mode")
        @Override
        public Mode mode() {
            return Mode.BASIC;
        }
    };

    @Data
    class Bearer implements AuthenticationRepresentation {
        private String authorizationServerUrl;

        @Override
        public Mode mode() {
            return Mode.BEARER;
        }
    }
}
