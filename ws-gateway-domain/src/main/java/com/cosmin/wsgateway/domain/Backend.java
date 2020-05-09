package com.cosmin.wsgateway.domain;

public interface Backend<T extends BackendSettings> {
    enum Type {
        HTTP,
        KAFKA
    }

    String destination();

    T settings();

    Type type();
}
