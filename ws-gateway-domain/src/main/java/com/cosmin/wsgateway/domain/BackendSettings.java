package com.cosmin.wsgateway.domain;

public interface BackendSettings {
    class Empty implements BackendSettings {
    }

    static Empty empty() {
        return new Empty();
    }
}
