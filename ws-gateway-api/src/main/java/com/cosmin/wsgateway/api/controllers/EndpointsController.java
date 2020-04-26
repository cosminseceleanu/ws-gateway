package com.cosmin.wsgateway.api.controllers;

import com.cosmin.wsgateway.api.mappers.EndpointMapper;
import com.cosmin.wsgateway.api.representation.EndpointRepresentation;
import com.cosmin.wsgateway.application.configuration.services.EndpointWriter;
import com.cosmin.wsgateway.application.configuration.services.EndpointsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class EndpointsController {

    private final EndpointMapper endpointMapper;
    private final EndpointsProvider endpointsProvider;
    private final EndpointWriter endpointWriter;

    @GetMapping("/endpoints")
    public Flux<EndpointRepresentation> getAll() {
        return endpointsProvider.getAll()
                .map(endpointMapper::toRepresentation);
    }

    @GetMapping("/endpoints/{id}")
    public Mono<EndpointRepresentation> get(@PathVariable String id) {
        return endpointsProvider.getById(id).map(endpointMapper::toRepresentation);
    }

    @PostMapping(value = "/endpoints")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EndpointRepresentation> create(@NotNull @RequestBody EndpointRepresentation representation) {
        return Mono.just(representation).map(endpointMapper::toModel)
                .flatMap(endpointWriter::create)
                .map(endpointMapper::toRepresentation);
    }

    @PutMapping("/endpoints/{id}")
    public Mono<EndpointRepresentation> update(
            @PathVariable String id,
            @NotNull EndpointRepresentation representation
    ) {
        return Mono.just(representation).map(endpointMapper::toModel)
                .flatMap(e -> endpointWriter.update(id, e))
                .map(endpointMapper::toRepresentation);
    }

    @DeleteMapping("/endpoints")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return endpointWriter.delete(id).map(e -> null);
    }
}
