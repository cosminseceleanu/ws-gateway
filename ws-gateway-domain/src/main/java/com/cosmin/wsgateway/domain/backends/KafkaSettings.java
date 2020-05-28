package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.BackendSettings;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class KafkaSettings implements BackendSettings {
    @NotNull
    private final String bootstrapServers;
}
