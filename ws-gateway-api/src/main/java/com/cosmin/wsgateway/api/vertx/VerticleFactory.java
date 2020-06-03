package com.cosmin.wsgateway.api.vertx;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.connection.ConnectionManager;
import com.cosmin.wsgateway.infrastructure.GatewayProperties;
import io.vertx.core.Verticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VerticleFactory {
    private final ConnectionManager connectionManager;
    private final GatewayMetrics gatewayMetrics;
    private final GatewayProperties gatewayProperties;

    public Verticle createWS() {
        return new WebSocketServer(connectionManager, gatewayMetrics, gatewayProperties);
    }
}
