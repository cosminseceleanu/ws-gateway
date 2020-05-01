package com.cosmin.wsgateway.application.gateway.connection;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.connector.ConnectorResolver;
import com.cosmin.wsgateway.domain.Endpoint;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
public class ConnectionFactory {
    private final PubSub pubSub;
    private final PayloadTransformer transformer;
    private final ConnectorResolver connectorResolver;
    private final Environment environment;

    public Connection create(Endpoint endpoint, ConnectionRequest request) {
        String connectionId = getConnectionId(request);
        var context = ConnectionContext.newInstance(connectionId, endpoint);
        log.debug("A new WS was created! connection={} for endpointId={} path={}",
                connectionId, endpoint.getId(), endpoint.getPath()
        );

        return new Connection(pubSub, transformer, connectorResolver, context);
    }

    private String getConnectionId(ConnectionRequest request) {
        if (request.getQueryParams().containsKey("cid") && environment.acceptsProfiles(Profiles.of("tests"))) {
            return request.getQueryParams().get("cid");
        }

        return UUID.randomUUID().toString();
    }
}
