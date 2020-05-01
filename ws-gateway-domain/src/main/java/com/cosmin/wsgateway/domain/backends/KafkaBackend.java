package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

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
