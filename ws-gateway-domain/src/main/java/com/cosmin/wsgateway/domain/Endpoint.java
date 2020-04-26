package com.cosmin.wsgateway.domain;

import com.cosmin.wsgateway.domain.exceptions.RouteNotFoundException;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static com.cosmin.wsgateway.domain.Route.Type.*;

@Value
@Builder
@With
public class Endpoint {
    private final String id;

    @NotNull
    @Size(min = 2, max = 255)
    @Pattern(regexp="^(?!\\/api\\/internal).*")
    private final String path;

    @NotNull
    @Valid
    private final EndpointConfiguration configuration;

    public Route getConnectRoute() {
        return getRoute(CONNECT).orElseThrow(() -> new RouteNotFoundException("Connect Route is not defined"));
    }

    public Route getDisconnectRoute() {
        return getRoute(DISCONNECT).orElseThrow(() -> new RouteNotFoundException("Disconnect Route is not defined"));
    }

    public Route getDefaultRoute() {
        return getRoute(DEFAULT).orElseThrow(() -> new RouteNotFoundException("Default Route is not defined"));
    }

    public Boolean hasRoute(Route.Type type) {
        return getRoute(type).isPresent();
    }

    public Optional<Route> getRoute(Route.Type type) {
        return configuration.getRoutesByType(type)
                .stream()
                .filter(r -> r.getType().equals(type))
                .findFirst();
    }

    public Endpoint addRoutes(Set<Route> routes) {
        Set<Route> newRoutes = new LinkedHashSet<>(getRoutes());
        newRoutes.addAll(routes);
        EndpointConfiguration newConfiguration = configuration.withRoutes(newRoutes);

        return withConfiguration(newConfiguration);
    }

    public Set<Route> getRoutes() {
        return configuration.getRoutes();
    }

    public Authentication getAuthentication() {
        return configuration.getAuthentication();
    }

    public Set<Filter<?>> getFilters() {
        return configuration.getFilters();
    }

    public Boolean matchesPath(String targetPath) {
        return targetPath.matches(path);
    }
}
