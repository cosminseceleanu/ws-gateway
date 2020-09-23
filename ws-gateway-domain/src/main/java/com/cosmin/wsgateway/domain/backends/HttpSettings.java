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
    private static final Map<String, String> DEFAULT_ADDITIONAL_HEADERS = Map.of();
    private static final int READ_TIMEOUT_IN_MILLIS = 1500;
    private static final int DEFAULT_CONNECT_TIMEOUT_IN_MILLIS = 200;

    @Size(max = 255)
    @NotNull
    private final Map<String, String> additionalHeaders;

    @Min(10)
    @Max(600000)
    @NotNull
    private final Integer readTimeoutInMillis;

    @Min(10)
    @Max(600000)
    @NotNull
    private final Integer connectTimeoutInMillis;

    public static HttpSettings ofDefaults() {
        return HttpSettings.builder()
                .additionalHeaders(DEFAULT_ADDITIONAL_HEADERS)
                .readTimeoutInMillis(READ_TIMEOUT_IN_MILLIS)
                .connectTimeoutInMillis(DEFAULT_CONNECT_TIMEOUT_IN_MILLIS)
                .build();
    }
}
