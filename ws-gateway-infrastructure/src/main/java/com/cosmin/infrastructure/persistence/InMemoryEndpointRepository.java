package com.cosmin.infrastructure.persistence;

import com.cosmin.wsgateway.api.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import com.cosmin.wsgateway.domain.model.Endpoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEndpointRepository implements EndpointRepository {
    private final ConcurrentHashMap<String, Endpoint> storage = new ConcurrentHashMap<>();

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
