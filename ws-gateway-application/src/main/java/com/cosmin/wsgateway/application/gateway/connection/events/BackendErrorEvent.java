package com.cosmin.wsgateway.application.gateway.connection.events;

import com.cosmin.wsgateway.domain.Event;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor(staticName = "of")
@Value
public class BackendErrorEvent implements InternalEvents {
    private final Event initialEvent;
    private final Throwable error;

    @Override
    public String connectionId() {
        return initialEvent.connectionId();
    }

    @Override
    public Object payload() {
        return initialEvent.payload();
    }

    public Throwable getError() {
        return error;
    }
}
