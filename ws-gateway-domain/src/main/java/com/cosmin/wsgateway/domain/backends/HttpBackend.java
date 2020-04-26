package com.cosmin.wsgateway.domain.backends;

import com.cosmin.wsgateway.domain.Backend;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class HttpBackend implements Backend<HttpSettings> {
    @NonNull
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
}
