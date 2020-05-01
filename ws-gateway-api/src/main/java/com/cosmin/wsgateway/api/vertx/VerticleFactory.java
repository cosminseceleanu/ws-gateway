package com.cosmin.wsgateway.api.vertx;

import com.cosmin.wsgateway.application.gateway.connection.ConnectionManager;
import io.vertx.core.Verticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VerticleFactory {
    private final ConnectionManager connectionManager;

    public Verticle createWS() {
        return new WebSocketServer(connectionManager);
    }
}
