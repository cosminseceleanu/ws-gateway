package com.cosmin.wsgateway.application.gateway.connection;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.pipeline.StagesProvider;
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
    private final StagesProvider stagesProvider;
    private final Environment environment;
    private final GatewayMetrics gatewayMetrics;

    public Connection create(Endpoint endpoint, ConnectionRequest request) {
        String connectionId = getConnectionId(request);
        var context = Connection.Context.newInstance(connectionId, endpoint);
        log.info("A new WS connection was created! {} for {} {}",
                keyValue("connectionId", connectionId),
                keyValue("endpointId", endpoint.getId()),
                keyValue("path", endpoint.getPath())
        );

        return new Connection(pubSub, context, gatewayMetrics, stagesProvider);
    }

    private String getConnectionId(ConnectionRequest request) {
        if (request.getQueryParams().containsKey("cid") && environment.acceptsProfiles(Profiles.of("tests"))) {
            return request.getQueryParams().get("cid");
        }

        return UUID.randomUUID().toString();
    }
}
