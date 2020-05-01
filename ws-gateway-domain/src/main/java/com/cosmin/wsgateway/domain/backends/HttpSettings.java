package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.BackendSettings;
import java.util.Map;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HttpSettings implements BackendSettings {
    @Size(max = 255)
    @NotNull
    private final Map<String, String> additionalHeaders;

    @Min(10)
    @Max(600000)
    @NotNull
    private final Integer timeoutInMillis;
}
