package com.cosmin.wsgateway.domain.events;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString()
@RequiredArgsConstructor
public class WSMessage implements InboundEvent {
    private final String connectionId;
    private final Object payload;

    @Override
    public String connectionId() {
        return connectionId;
    }

    @Override
    public Object payload() {
        return payload;
    }
}
