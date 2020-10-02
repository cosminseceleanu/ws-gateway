package com.cosmin.wsgateway.infrastructure.integrations.mongo;

import com.cosmin.wsgateway.application.configuration.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.infrastructure.integrations.mongo.mapper.EndpointDocumentMapper;
import com.cosmin.wsgateway.infrastructure.integrations.mongo.repository.SpringEndpointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MongoEndpointRepository implements EndpointRepository {

    private final SpringEndpointRepository repository;
    private final EndpointDocumentMapper mapper;

    @Override
    public Flux<Endpoint> getAll() {
        return repository.findAll()
                .map(mapper::toDomainObject);
    }

    @Override
    public Mono<Endpoint> getById(String id) {
        return repository.findById(id).map(mapper::toDomainObject);
    }

    @Override
    public Mono<Endpoint> save(Endpoint endpoint) {
        return repository.save(mapper.toDocument(endpoint))
                .map(mapper::toDomainObject);
    }

    @Override
    public Mono<Endpoint> deleteById(String id) {
        return getById(id)
                .flatMap(e -> repository.delete(mapper.toDocument(e)).map(r -> e));
    }
}
