package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralSettingsRepresentation {
    public static final Integer DEFAULT_BACKEND_PARALLELISM = 8;
    public static final int DEFAULT_HEARTBEAT_INTERVAL = 15;
    public static final int DEFAULT_MISSING_PING_FRAMES = 3;

    private Integer backendParallelism = DEFAULT_BACKEND_PARALLELISM;

    private Integer heartbeatIntervalInSeconds = DEFAULT_HEARTBEAT_INTERVAL;

    private Integer heartbeatMaxMissingPingFrames = DEFAULT_MISSING_PING_FRAMES;
}
