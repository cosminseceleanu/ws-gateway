package com.cosmin.wsgateway.api.controllers;

import com.cosmin.wsgateway.api.rest.representation.EndpointRepresentation;
import com.cosmin.wsgateway.api.services.EndpointWriter;
import com.cosmin.wsgateway.api.services.EndpointsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class EndpointsController {

    private final EndpointsProvider endpointsProvider;
    private final EndpointWriter endpointWriter;

    @GetMapping("/endpoints")
    public Flux<EndpointRepresentation> getAll() {
        return null;
    }

    @GetMapping("/endpoints/{id}")
    private Mono<EndpointRepresentation> getById(@PathVariable String id) {
        return endpointsProvider.getById(id).map(e -> null);
    }
}
