package com.cosmin.wsgateway.domain.model.backends;

import com.cosmin.wsgateway.domain.model.Backend;
import com.cosmin.wsgateway.domain.model.BackendSettings;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Value
public class KafkaBackend implements Backend<BackendSettings.Empty> {
    @NotNull
    @Min(5)
    @Max(255)
    private final String topic;

    @Override
    public String destination() {
        return topic;
    }

    @Override
    public BackendSettings.Empty settings() {
        return BackendSettings.empty();
    }

    @Override
    public Type type() {
        return Type.KAFKA;
    }
}
