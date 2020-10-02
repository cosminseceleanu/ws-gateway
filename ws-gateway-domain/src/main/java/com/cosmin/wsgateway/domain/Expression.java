package com.cosmin.wsgateway.domain;

import com.cosmin.wsgateway.domain.exceptions.IncorrectExpressionException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface Expression<T> {

    @RequiredArgsConstructor
    @Getter
    enum Name {
        EQUAL("equal"),
        MATCHES("matches"),
        OR("or"),
        AND("and");
        private final String value;

        private static final Map<String, Name> namesByValue = Arrays.stream(values())
                .collect(Collectors.toMap(Name::getValue, Function.identity()));

        public static Name fromValue(String name) {
            return Optional.ofNullable(namesByValue.get(name))
                    .orElseThrow(() -> new IncorrectExpressionException(
                            String.format("Expression %s is not defined", name)
                    ));
        }
    }

    Name name();

    T evaluate(String json);
}
