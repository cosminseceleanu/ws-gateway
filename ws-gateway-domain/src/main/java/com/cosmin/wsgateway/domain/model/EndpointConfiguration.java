package com.cosmin.wsgateway.domain.model;

import com.cosmin.wsgateway.domain.validation.constraints.ValidRouteConfiguration;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Value
@Builder
@ValidRouteConfiguration
@With
public class EndpointConfiguration {
    @NotNull
    @Size(max = 255)
    @Valid
    private final Set<Filter<Object>> filters;

    @NotNull
    @Size(max = 255)
    @Valid
    private final Set<Route> routes;

    @NotNull
    @Valid
    private final Authentication authentication;

    @NotNull
    private final LocalDateTime createdAt;

    public Set<Route> getRoutesByType(Route.Type type) {
        return routes.stream()
                .filter(r -> r.getType().equals(type))
                .collect(toUnmodifiableSet());
    }

    public Set<Filter<Object>> getFilters() {
        return Collections.unmodifiableSet(filters);
    }

    public Set<Route> getRoutes() {
        return Collections.unmodifiableSet(routes);
    }
}
