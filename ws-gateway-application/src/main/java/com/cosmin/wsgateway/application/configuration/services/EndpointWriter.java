package com.cosmin.wsgateway.application.configuration.services;

import static java.util.stream.Collectors.toSet;

import com.cosmin.wsgateway.application.configuration.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Component
@Validated
@RequiredArgsConstructor
public class EndpointWriter {
    private final EndpointRepository endpointRepository;
    private final EndpointsProvider endpointsProvider;

    private final Map<Route.Type, Route> defaultRoutes = Map.of(
            Route.Type.CONNECT, Route.connect(),
            Route.Type.DISCONNECT, Route.disconnect(),
            Route.Type.DEFAULT, Route.defaultRoute()
    );

    public Mono<Endpoint> create(@Valid Endpoint endpoint) {
        Set<Route> missingRoutes = getMissingRoutes(endpoint);
        Endpoint enrichedEndpoint = endpoint.addRoutes(missingRoutes);

        return endpointRepository.save(enrichedEndpoint);
    }

    private Set<Route> getMissingRoutes(Endpoint endpoint) {
        return defaultRoutes.entrySet()
                    .stream()
                    .filter(e -> !endpoint.hasRoute(e.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(toSet());
    }

    public Mono<Endpoint> update(String id, @Valid Endpoint endpoint) {
        return endpointsProvider.getById(id)
                .flatMap(existing -> {
                    Endpoint updated = existing.withPath(endpoint.getPath())
                            .withConfiguration(endpoint.getConfiguration());

                    return endpointRepository.save(updated);
                });
    }

    public Mono<Endpoint> delete(String id) {
        return endpointsProvider.getById(id)
                .flatMap(e -> endpointRepository.deleteById(id))
                .switchIfEmpty(Mono.error(new EndpointNotFoundException(id)));
    }
}
