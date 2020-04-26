package com.cosmin.infrastructure.persistence;

import com.cosmin.wsgateway.application.configuration.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import com.cosmin.wsgateway.domain.Endpoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEndpointRepository implements EndpointRepository {
    private final ConcurrentHashMap<String, Endpoint> storage = new ConcurrentHashMap<>();

    public InMemoryEndpointRepository() {
        var configuration = EndpointConfiguration.builder()
                .authentication(new Authentication.None())
                .filters(Collections.emptySet())
                .routes(Set.of(
                        Route.defaultRoute(),
                        Route.connect(),
                        Route.disconnect()
                ))
                .build();
        Endpoint endpoint = Endpoint.builder()
                .configuration(configuration)
                .id(UUID.randomUUID().toString())
                .path("/test")
                .build();
        storage.put(endpoint.getId(), endpoint);
    }

    @Override
    public Flux<Endpoint> getAll() {
        return Flux.fromIterable(storage.values());
    }

    @Override
    public Mono<Optional<Endpoint>> getById(String id) {
        return Mono.just(Optional.ofNullable(storage.get(id)));
    }

    @Override
    public Mono<Endpoint> save(Endpoint endpoint) {
        String id = UUID.randomUUID().toString();
        Endpoint result = endpoint.withId(id);
        storage.put(id, result);

        return Mono.just(result);
    }

    @Override
    public Mono<Endpoint> deleteById(String id) {
        return getById(id)
            .map(e -> e.orElseThrow(() -> new EndpointNotFoundException(id)))
            .doOnSuccess(e -> storage.remove(e.getId()));
    }
}
