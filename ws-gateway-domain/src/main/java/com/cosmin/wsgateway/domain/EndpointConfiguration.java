package com.cosmin.wsgateway.domain;

import static java.util.stream.Collectors.toUnmodifiableSet;

import com.cosmin.wsgateway.domain.validation.constraints.ValidRouteConfiguration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;


@Value
@Builder
@ValidRouteConfiguration
@With
public class EndpointConfiguration {
    @NotNull
    @Size(max = 255)
    @Valid
    private final Set<Filter<?>> filters;

    @NotNull
    @Size(max = 255)
    @Valid
    private final Set<Route> routes;

    @NotNull
    @Valid
    private final Authentication authentication;

    @Valid
    @NotNull
    private final GeneralSettings generalSettings;

    @NotNull
    @EqualsAndHashCode.Exclude
    private final LocalDateTime createdAt = LocalDateTime.now();

    public static EndpointConfiguration ofRoutes(Set<Route> routes) {
        return EndpointConfiguration.builder()
                .authentication(new Authentication.None())
                .generalSettings(GeneralSettings.ofDefaults())
                .filters(Collections.emptySet())
                .routes(routes)
                .build();
    }

    public Set<Route> getRoutesByType(Route.Type type) {
        return routes.stream()
                .filter(r -> r.getType().equals(type))
                .collect(toUnmodifiableSet());
    }

    public Set<Filter<?>> getFilters() {
        return Collections.unmodifiableSet(filters);
    }

    public Set<Route> getRoutes() {
        if (routes == null) {
            return null;
        }
        return Collections.unmodifiableSet(routes);
    }
}
