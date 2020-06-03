package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.representation.EndpointRepresentation;
import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.Filter;
import com.cosmin.wsgateway.domain.GeneralSettings;
import com.cosmin.wsgateway.domain.Route;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EndpointMapper implements RepresentationMapper<EndpointRepresentation, Endpoint> {
    private final RouteMapper routeMapper;
    private final AuthenticationMapper authenticationMapper;
    private final FilterMapper filterMapper;

    @Override
    public Endpoint toModel(EndpointRepresentation representation) {
        Set<Route> routes = Set.copyOf(routeMapper.toModels(representation.getRoutes()));
        Set<Filter<?>> filters = filterMapper.toModel(representation.getFilters());
        Authentication authentication = authenticationMapper.toModel(representation.getAuthentication());

        EndpointConfiguration configuration = EndpointConfiguration.builder()
                .authentication(authentication)
                .generalSettings(GeneralSettings
                        .builder()
                        .backendParallelism(representation.getGeneralSettings().getBackendParallelism())
                        .build())
                .filters(filters)
                .routes(routes)
                .build();

        return Endpoint.builder()
                .id(representation.getId())
                .path(representation.getPath())
                .configuration(configuration)
                .build();
    }

    @Override
    public EndpointRepresentation toRepresentation(Endpoint domain) {
        EndpointRepresentation representation = EndpointRepresentation.builder()
                .id(domain.getId())
                .path(domain.getPath())
                .build();
        representation.setAuthentication(authenticationMapper.toRepresentation(domain.getAuthentication()));
        representation.setFilters(filterMapper.toRepresentation(domain.getFilters()));
        representation.setRoutes(Set.copyOf(routeMapper.toRepresentations(domain.getRoutes())));

        return representation;
    }
}
