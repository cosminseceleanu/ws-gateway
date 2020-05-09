package com.cosmin.wsgateway.domain.events;

import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Route;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class Connected implements InboundEvent {
    private final String connectionId;

    @Override
    public String connectionId() {
        return connectionId;
    }

    @Override
    public Object payload() {
        return Map.of("connectionId", connectionId);
    }

    @Override
    public Route getRoute(Endpoint endpoint) {
        return endpoint.getConnectRoute();
    }
}