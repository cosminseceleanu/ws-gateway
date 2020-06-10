package com.cosmin.wsgateway.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeneralSettings {
    @NotNull
    @Min(1)
    @Max(32)
    private final Integer backendParallelism;

    public static GeneralSettings defaultSettings() {
        return GeneralSettings.builder()
                .backendParallelism(8)
                .build();
    }
}
