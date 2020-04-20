package com.cosmin.wsgateway.domain.model.backends;

import com.cosmin.wsgateway.domain.model.Backend;
import com.cosmin.wsgateway.domain.model.BackendSettings;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder
public class KafkaBackend implements Backend<BackendSettings.Empty> {
    @NotNull
    @Size(min = 5, max = 255)
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
