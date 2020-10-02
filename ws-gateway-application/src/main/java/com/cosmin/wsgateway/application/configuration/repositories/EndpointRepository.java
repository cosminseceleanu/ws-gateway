package com.cosmin.wsgateway.application.configuration.repositories;

import com.cosmin.wsgateway.domain.Endpoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface EndpointRepository {
    Flux<Endpoint> getAll();

    Mono<Endpoint> getById(String id);

    Mono<Endpoint> save(Endpoint endpoint);

    Mono<Endpoint> deleteById(String id);
}
