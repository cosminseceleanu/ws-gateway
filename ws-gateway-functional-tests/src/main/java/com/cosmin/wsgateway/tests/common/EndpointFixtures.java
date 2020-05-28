package com.cosmin.wsgateway.tests.common;

import static com.cosmin.wsgateway.api.representation.RouteRepresentation.Type.CONNECT;
import static com.cosmin.wsgateway.api.representation.RouteRepresentation.Type.DEFAULT;
import static com.cosmin.wsgateway.api.representation.RouteRepresentation.Type.DISCONNECT;

import com.cosmin.wsgateway.api.representation.AuthenticationRepresentation;
import com.cosmin.wsgateway.api.representation.EndpointRepresentation;
import com.cosmin.wsgateway.api.representation.FilterRepresentation;
import com.cosmin.wsgateway.api.representation.HttpBackendRepresentation;
import com.cosmin.wsgateway.api.representation.RouteRepresentation;
import java.util.Collections;
import java.util.Set;

public final class EndpointFixtures {

    private EndpointFixtures() {
    }

    public static EndpointRepresentation defaultRepresentation() {
        return getRepresentation("/ws-gateway-default");
    }

    public static EndpointRepresentation getRepresentation(String path, Set<RouteRepresentation> routes) {
        var representation = getRepresentation(path);
        representation.setRoutes(routes);

        return representation;
    }

    public static EndpointRepresentation getRepresentation(String path) {
        return EndpointRepresentation.builder()
                .path(path)
                .authentication(new AuthenticationRepresentation.None())
                .routes(Set.of(
                        createEmptyRoute("Connect", CONNECT),
                        createEmptyRoute("Disconnect", DISCONNECT),
                        createEmptyRoute("Default", DEFAULT)
                ))
                .filters(new FilterRepresentation())
                .build();
    }

    public static RouteRepresentation createEmptyRoute(String name, RouteRepresentation.Type type) {
        return RouteRepresentation.builder()
                .name(name)
                .type(type)
                .backends(Collections.emptySet())
                .build();
    }

    public static RouteRepresentation createRouteWithHttpBackend(String name, RouteRepresentation.Type type) {
        return createRouteWithHttpBackend(name, type, "http://localhost:8000");
    }

    public static RouteRepresentation createRouteWithHttpBackend(RouteRepresentation.Type type, String backendUrl) {
        return createRouteWithHttpBackend(type.getValue(), type, backendUrl);
    }

    public static RouteRepresentation createRouteWithHttpBackend(
            String name, RouteRepresentation.Type type, String backendUrl
    ) {
        return RouteRepresentation.builder()
                .name(name)
                .type(type)
                .backends(Set.of(
                        HttpBackendRepresentation.builder().destination(backendUrl).build()
                )).build();
    }
}
