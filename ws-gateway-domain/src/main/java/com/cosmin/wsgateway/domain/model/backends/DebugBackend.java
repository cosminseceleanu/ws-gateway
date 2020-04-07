package com.cosmin.wsgateway.domain.model.backends;

import com.cosmin.wsgateway.domain.model.Backend;
import com.cosmin.wsgateway.domain.model.BackendSettings;

public class DebugBackend implements Backend<BackendSettings.Empty> {
    @Override
    public String destination() {
        return "";
    }

    @Override
    public BackendSettings.Empty settings() {
        return BackendSettings.empty();
    }

    @Override
    public Type type() {
        return Type.DEBUG;
    }
}
