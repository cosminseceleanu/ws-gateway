package com.cosmin.wsgateway.api.representation;

import lombok.Data;

@Data
public class GeneralSettingsRepresentation {
    private static final Integer DEFAULT_BACKEND_PARALLELISM = 8;

    private Integer backendParallelism = DEFAULT_BACKEND_PARALLELISM;
}
