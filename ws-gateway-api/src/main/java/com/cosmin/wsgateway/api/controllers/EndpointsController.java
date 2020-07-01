package com.cosmin.wsgateway.api.controllers;

import com.cosmin.wsgateway.api.mappers.EndpointMapper;
import com.cosmin.wsgateway.api.representation.EndpointRepresentation;
import com.cosmin.wsgateway.application.configuration.services.EndpointWriter;
import com.cosmin.wsgateway.application.configuration.services.EndpointsProvider;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@Slf4j
public class EndpointsController {

    private final EndpointMapper endpointMapper;
    private final EndpointsProvider endpointsProvider;
    private final EndpointWriter endpointWriter;

    @GetMapping("/endpoints")
    public Flux<EndpointRepresentation> getAll() {
        log.info("Get all endpoints");
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
            @NotNull @RequestBody EndpointRepresentation representation
    ) {
        return Mono.just(representation).map(endpointMapper::toModel)
                .flatMap(e -> endpointWriter.update(id, e))
                .map(endpointMapper::toRepresentation);
    }

    @DeleteMapping("/endpoints/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<EndpointRepresentation> delete(@PathVariable String id) {
        return endpointWriter.delete(id).map(endpointMapper::toRepresentation);
    }
}
