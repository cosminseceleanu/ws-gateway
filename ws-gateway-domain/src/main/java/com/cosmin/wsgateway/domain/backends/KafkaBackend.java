package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class KafkaBackend implements Backend<KafkaSettings> {
    @NotNull
    @Size(min = 5, max = 255)
    private final String topic;

    @NotNull
    @Valid
    private final KafkaSettings settings;

    @Override
    public String destination() {
        return topic;
    }

    @Override
    public KafkaSettings settings() {
        return settings;
    }

    @Override
    public Type type() {
        return Type.KAFKA;
    }
}
