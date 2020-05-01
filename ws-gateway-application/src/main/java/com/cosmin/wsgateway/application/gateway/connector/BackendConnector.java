package com.cosmin.wsgateway.application.gateway.connector;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Event;
import reactor.core.publisher.Mono;

public interface BackendConnector<T extends BackendSettings> {
    boolean supports(Backend.Type type);

    Mono<Event> sendEvent(Event event, Backend<T> backend);
}
