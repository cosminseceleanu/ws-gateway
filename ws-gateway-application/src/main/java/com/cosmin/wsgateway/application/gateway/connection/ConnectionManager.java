package com.cosmin.wsgateway.application.gateway.connection;

import com.cosmin.wsgateway.application.configuration.services.EndpointsProvider;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.domain.Endpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectionManager {
    private final EndpointsProvider endpointsProvider;
    private final PubSub pubSub;
    private final PayloadTransformer transformer;

    public Mono<Connection> connect(ConnectionRequest request) {
        return endpointsProvider.getFirstMatch(request.getPath())
                .map(this::createConnectionBridge);
    }

    private Connection createConnectionBridge(Endpoint endpoint) {
        String connectionId = UUID.randomUUID().toString();
        var context = ConnectionContext.newInstance(connectionId, endpoint);
        log.debug("A new WS was created! connection={} for endpointId={} path={}", connectionId, endpoint.getId(), endpoint.getPath());

        return new Connection(pubSub, transformer, context);
    }
}
