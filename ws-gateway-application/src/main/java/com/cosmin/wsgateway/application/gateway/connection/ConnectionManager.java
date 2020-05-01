package com.cosmin.wsgateway.application.gateway.connection;

import com.cosmin.wsgateway.application.configuration.services.EndpointsProvider;
import com.cosmin.wsgateway.application.gateway.connection.filters.ConnectionFilter;
import com.cosmin.wsgateway.domain.Endpoint;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectionManager {
    private final EndpointsProvider endpointsProvider;
    private final ConnectionFactory connectionFactory;
    private final List<ConnectionFilter> filters;

    public Mono<Connection> connect(ConnectionRequest request) {
        return endpointsProvider.getFirstMatch(request.getPath())
                .flatMap(e -> runConnectionFilters(request, e))
                .map(endpoint -> connectionFactory.create(endpoint, request));
    }

    private Mono<Endpoint> runConnectionFilters(ConnectionRequest request, Endpoint endpoint) {
        return filters.stream()
                .reduce(Mono.just(request), (acc, filter) -> acc.flatMap(runFilter(endpoint, filter)), (r1, r2) -> r2)
                .map(connectionRequest -> endpoint);
    }

    private Function<ConnectionRequest, Mono<? extends ConnectionRequest>> runFilter(
            Endpoint endpoint, ConnectionFilter filter
    ) {
        return connectionRequest -> filter.filter(endpoint, connectionRequest);
    }
}
