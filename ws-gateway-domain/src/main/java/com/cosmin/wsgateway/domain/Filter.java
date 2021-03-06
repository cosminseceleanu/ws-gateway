package com.cosmin.wsgateway.domain;

import java.util.Map;

public interface Filter<T> {
    String name();

    T value();

    boolean isAllowed(Map<String, String> requestHeaders);
}
