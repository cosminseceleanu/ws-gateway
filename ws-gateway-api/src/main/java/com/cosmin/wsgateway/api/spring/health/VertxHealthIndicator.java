package com.cosmin.wsgateway.api.spring.health;

import com.cosmin.wsgateway.infrastructure.GatewayProperties;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.net.impl.ServerID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VertxHealthIndicator extends AbstractHealthIndicator {

    private final Vertx vertx;
    private final GatewayProperties gatewayProperties;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        var httpServer = ((VertxImpl) vertx).sharedHttpServers()
                .get(new ServerID(gatewayProperties.getVertx().getGatewayPort(), "0.0.0.0"));
        if (httpServer == null) {
            builder.unknown();
            return;
        }
        if (httpServer.isClosed()) {
            builder.down();
            return;
        }

        builder.up();
    }
}
