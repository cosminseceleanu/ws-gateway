package com.cosmin.wsgateway.domain;

import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import com.cosmin.wsgateway.domain.backends.KafkaSettings;
import java.util.Collections;
import java.util.Set;

public class Fixtures {
    private Fixtures() {}

    public static String sampleJson() {
        return "{\"array\":[1,2,3],\"boolean\":true,\"color\":\"gold\",\"null\":null,\"number\":123,\"object\":{\"a\":\"b\",\"c\":\"d\"},\"string\":\"Hello World\"}";
    }

    public static Endpoint defaultEndpoint() {

        var configuration = EndpointConfiguration.builder()
                .authentication(new Authentication.None())
                .filters(Collections.emptySet())
                .generalSettings(defaultGeneralSettings())
                .routes(Set.of(
                        getDefaultRoute(),
                        getConnectRoute(),
                        getDisconnectRoute()
                ))
                .build();

        return Endpoint.builder()
                .configuration(configuration)
                .id("id")
                .path("/test")
                .build();
    }

    public static Route getConnectRoute() {
        return Route.connect(getDefaultBackends());
    }

    public static Route getDefaultRoute() {
        return Route.defaultRoute(getDefaultBackends());
    }

    public static Route getDisconnectRoute() {
        return Route.disconnect(getDefaultBackends());
    }

    private static Set<Backend<? extends BackendSettings>> getDefaultBackends() {
        return Collections.singleton(
                HttpBackend.builder().destination("http://example.com").settings(
                        defaultHttpSettings()
                ).build()
        );
    }

    public static GeneralSettings defaultGeneralSettings() {
        return GeneralSettings.builder()
                .backendParallelism(8)
                .build();
    }

    public static HttpSettings defaultHttpSettings() {
        return HttpSettings.builder()
                .additionalHeaders(Collections.emptyMap())
                .readTimeoutInMillis(200)
                .connectTimeoutInMillis(100)
                .build();
    }

    public static KafkaSettings defaultKafkaSettings() {
        return KafkaSettings.builder()
                .bootstrapServers("server")
                .acks(KafkaSettings.Ack.NO_ACK)
                .retriesNr(0)
                .build();
    }
}
