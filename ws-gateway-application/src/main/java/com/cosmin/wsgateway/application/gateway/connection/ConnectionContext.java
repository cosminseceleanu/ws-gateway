package com.cosmin.wsgateway.application.gateway.connection;

import com.cosmin.wsgateway.domain.Endpoint;
import lombok.Value;

@Value
public class ConnectionContext {
    private final String id;
    private final Endpoint endpoint;

    public static ConnectionContext newInstance(String id, Endpoint endpoint) {
        return new ConnectionContext(id, endpoint);
    }
}
