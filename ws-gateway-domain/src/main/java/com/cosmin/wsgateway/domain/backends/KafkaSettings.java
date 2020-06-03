package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.BackendSettings;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
public class KafkaSettings implements BackendSettings {
    @NotNull
    private final String bootstrapServers;

    @NotNull
    private final Ack acks;

    @Min(0)
    @Max(10)
    private final int retriesNr;

    @RequiredArgsConstructor
    @Getter
    public enum Ack {
        NO_ACK("0"),
        LEADER("1"),
        ALL("all");

        private final String kafkaValue;
    }
}
