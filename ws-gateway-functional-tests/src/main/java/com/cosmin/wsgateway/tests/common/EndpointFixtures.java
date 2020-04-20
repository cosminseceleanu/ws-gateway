package com.cosmin.wsgateway.tests.common;

import com.cosmin.wsgateway.api.representation.*;

import java.util.Set;

public final class EndpointFixtures {

    private EndpointFixtures() {
    }

    public static EndpointRepresentation defaultRepresentation() {
        return EndpointRepresentation.builder()
                .path("/ws-gateway-default")
                .authentication(new AuthenticationRepresentation.None())
                .routes(Set.of(
                        createRoute("Connect", RouteRepresentation.Type.CONNECT),
                        createRoute("Disconnect", RouteRepresentation.Type.DISCONNECT),
                        createRoute("Default", RouteRepresentation.Type.DEFAULT)
                ))
                .build();
    }

    private static RouteRepresentation createRoute(String connect, RouteRepresentation.Type type) {
        return RouteRepresentation.builder()
                .name(connect)
                .type(type)
                .backends(Set.of(
                        HttpBackendRepresentation.builder().destination("http://localhost:8000").build()
                )).build();
    }
}
