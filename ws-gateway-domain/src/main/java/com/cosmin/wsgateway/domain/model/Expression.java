package com.cosmin.wsgateway.domain.model;

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
    }

    Name name();
    T evaluate(String json);
}
