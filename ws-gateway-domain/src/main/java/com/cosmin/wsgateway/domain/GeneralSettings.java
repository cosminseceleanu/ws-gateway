package com.cosmin.wsgateway.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class GeneralSettings {

    public static final int DEFAULT_BACKEND_PARALLELISM = 8;
    public static final int DEFAULT_HEARTBEAT_INTERVAL_IN_SECONDS = 15;
    public static final int DEFAULT_HEARTBEAT_MAX_MISSING_PING_FRAMES = 3;

    @NotNull
    @Min(1)
    @Max(32)
    private final Integer backendParallelism;

    @Max(60)
    @Min(5)
    @NotNull
    private Integer heartbeatIntervalInSeconds;

    @Min(1)
    @Max(10)
    @NotNull
    private Integer heartbeatMaxMissingPingFrames;

    public static GeneralSettings ofDefaults() {
        return GeneralSettings.builder()
                .backendParallelism(DEFAULT_BACKEND_PARALLELISM)
                .heartbeatIntervalInSeconds(DEFAULT_HEARTBEAT_INTERVAL_IN_SECONDS)
                .heartbeatMaxMissingPingFrames(DEFAULT_HEARTBEAT_MAX_MISSING_PING_FRAMES)
                .build();
    }
}
