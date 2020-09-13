package com.cosmin.wsgateway.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class GeneralSettings {
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

    public static GeneralSettings defaultSettings() {
        return GeneralSettings.builder()
                .backendParallelism(8)
                .heartbeatIntervalInSeconds(15)
                .heartbeatMaxMissingPingFrames(3)
                .build();
    }
}
