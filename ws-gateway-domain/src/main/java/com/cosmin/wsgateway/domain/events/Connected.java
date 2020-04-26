package com.cosmin.wsgateway.domain.events;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

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
}
