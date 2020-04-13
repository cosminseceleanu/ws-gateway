package com.cosmin.wsgateway.domain.model;

import com.cosmin.wsgateway.domain.exceptions.IncorrectExpressionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Expression<T> {

    @RequiredArgsConstructor
    @Getter
    enum Name {
        EQUAL("equal"),
        MATCHES("matches"),
        OR("or"),
        AND("and");
        private final String value;

        private static Map<String, Name> namesByValue = Arrays.stream(values())
                .collect(Collectors.toMap(Name::getValue, Function.identity()));

        public static Name fromValue(String name) {
            return Optional.ofNullable(namesByValue.get(name))
                    .orElseThrow(() -> new IncorrectExpressionException(String.format("Expression %s is not defined", name)));
        }
    }

    Name name();
    T evaluate(String json);
}
