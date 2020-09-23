package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.Backend;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

@Value
@Builder
public class HttpBackend implements Backend<HttpSettings> {
    @NotNull
    @URL
    private final String destination;

    @NotNull
    @Valid
    private final HttpSettings settings;

    @Override
    public String destination() {
        return destination;
    }

    @Override
    public HttpSettings settings() {
        return settings;
    }

    @Override
    public Type type() {
        return Type.HTTP;
    }

    public static Backend<HttpSettings> ofDefaults(String endpoint) {
        return HttpBackend.builder()
                .settings(HttpSettings.ofDefaults())
                .destination(endpoint)
                .build();
    }
}
