package com.cosmin.wsgateway.application;

import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import java.util.Collections;
import java.util.Set;

public class Fixtures {
    private Fixtures() {}

    public static Endpoint defaultEndpoint() {

        var configuration = EndpointConfiguration.builder()
                .authentication(new Authentication.None())
                .filters(Collections.emptySet())
                .routes(Set.of(
                        Route.connect(getDefaultBackends()),
                        Route.defaultRoute(getDefaultBackends()),
                        Route.disconnect(getDefaultBackends())
                ))
                .build();
        return Endpoint.builder()
                .configuration(configuration)
                .id("id")
                .path("/test")
                .build();
    }


    private static Set<Backend<? extends BackendSettings>> getDefaultBackends() {
        return Collections.singleton(
                HttpBackend.builder().destination("http://example.com").settings(
                        HttpSettings.builder().additionalHeaders(Collections.emptyMap()).readTimeoutInMillis(200).build()
                ).build()
        );
    }
}
