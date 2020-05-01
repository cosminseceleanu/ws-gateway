package com.cosmin.wsgateway.api.fixtures;

import com.cosmin.wsgateway.api.representation.RouteRepresentation;

import java.util.Map;
import java.util.Set;

import static com.cosmin.wsgateway.api.fixtures.BackendFixtures.defaultHttpRepresentation;

public final class RouteFixtures {

    private RouteFixtures() {}

    public static RouteRepresentation connectRepresentation() {
        return RouteRepresentation.builder()
                .type(RouteRepresentation.Type.CONNECT)
                .name("Connect")
                .backends(Set.of(defaultHttpRepresentation()))
                .build();
    }

    public static RouteRepresentation customRouteRepresentation() {
        return RouteRepresentation.builder()
                .type(RouteRepresentation.Type.CUSTOM)
                .name("Custom")
                .backends(Set.of(defaultHttpRepresentation()))
                .expression(Map.of("and", "something"))
                .build();
    }
}
