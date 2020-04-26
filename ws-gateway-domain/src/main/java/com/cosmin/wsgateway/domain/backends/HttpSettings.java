package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.BackendSettings;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;

@Value
@Builder
public class HttpSettings implements BackendSettings {
    @Size(max = 255)
    private final Map<String, String> additionalHeaders;

    @Min(10)
    @Max(600000)
    private final Integer timeoutInMillis;
}
