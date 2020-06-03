package com.cosmin.wsgateway.api;

import lombok.Data;

@Data
public class GeneralSettingsRepresentation {
    private static final Integer DEFAULT_BACKEND_PARALLELISM = 8;

    private Integer backendParallelism = DEFAULT_BACKEND_PARALLELISM;
}
