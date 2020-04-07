package com.cosmin.wsgateway.domain.model;

public interface BackendSettings {
    class Empty implements BackendSettings {
    }

    static Empty empty() {
        return new Empty();
    }
}
