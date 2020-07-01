package com.cosmin.wsgateway.infrastructure.integrations.mock;

import com.cosmin.wsgateway.application.configuration.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.GeneralSettings;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(prefix = "gateway", name = "persistence.mocked")
public class InMemoryEndpointRepository implements EndpointRepository {
    private final ConcurrentHashMap<String, Endpoint> storage = new ConcurrentHashMap<>();

    public InMemoryEndpointRepository() {
        Set<Backend<? extends BackendSettings>> debugBackend = Collections.singleton(
                HttpBackend.builder()
                        .destination("http://gateway-mock-backend.ns-ws-gateway.svc.cluster.local:8083/events/default")
                        .settings(HttpSettings.defaultSettings())
                .build()
        );

        var configuration = EndpointConfiguration.builder()
                .authentication(new Authentication.None())
                .filters(Collections.emptySet())
                .routes(Set.of(
                        Route.defaultRoute(debugBackend),
                        Route.connect(debugBackend),
                        Route.disconnect(debugBackend)
                ))
                .generalSettings(GeneralSettings.defaultSettings())
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
