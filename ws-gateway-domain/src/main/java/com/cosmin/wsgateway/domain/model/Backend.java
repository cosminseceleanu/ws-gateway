package com.cosmin.wsgateway.domain.model;

public interface Backend<T extends BackendSettings> {
    enum Type {
        DEBUG,
        HTTP,
        KAFKA
    }

    String destination();
    T settings();
    Type type();
}
