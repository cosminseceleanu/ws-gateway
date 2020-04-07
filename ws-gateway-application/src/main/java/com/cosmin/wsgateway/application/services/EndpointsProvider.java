package com.cosmin.wsgateway.application.services;

import com.cosmin.wsgateway.application.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import com.cosmin.wsgateway.domain.model.Endpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EndpointsProvider {
    private final EndpointRepository endpointRepository;

    public Flux<Endpoint> getAll() {
        return endpointRepository.getAll();
    }

    public Mono<Endpoint> getById(String id) {
        return endpointRepository.getById(id)
                .map(e -> e.orElseThrow(() -> new EndpointNotFoundException(id)));
    }

    public Mono<Endpoint> getFirstMatch(String path) {
        return endpointRepository.getAll()
                .collectList()
                .map(endpoints -> getEndpointThatMatchesPath(path, endpoints));
    }

    private Endpoint getEndpointThatMatchesPath(String path, List<Endpoint> endpoints) {
        return endpoints.stream()
                .filter(e -> e.matchesPath(path))
                .findFirst()
                .orElseThrow(() -> new EndpointNotFoundException("path"));
    }
}
