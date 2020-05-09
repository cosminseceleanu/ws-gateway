package com.cosmin.wsgateway.domain.events;

import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Route;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class UserMessage implements InboundEvent {
    private final String connectionId;
    private final Object payload;
    private final String originalJson;

    @Override
    public String connectionId() {
        return connectionId;
    }

    @Override
    public Object payload() {
        return payload;
    }

    @Override
    public Route getRoute(Endpoint endpoint) {
        return endpoint.findCustomRoute(originalJson)
                .orElse(endpoint.getDefaultRoute());
    }
}
