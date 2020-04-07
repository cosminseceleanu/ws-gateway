package com.cosmin.wsgateway.domain.model.backends;

import com.cosmin.wsgateway.domain.model.BackendSettings;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;

@Value
public class HttpSettings implements BackendSettings {
    @Size(max = 255)
    private final Map<String, String> additionalHeaders;

    @Min(10)
    @Max(600000)
    private final Integer timeoutInMillis;
}
