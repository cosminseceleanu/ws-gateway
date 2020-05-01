package com.cosmin.wsgateway.application.gateway.connector;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DebugConnector implements BackendConnector<BackendSettings.Empty> {
    @Override
    public boolean supports(Backend.Type type) {
        return Backend.Type.DEBUG.equals(type);
    }

    @Override
    public Mono<Event> sendEvent(Event event, Backend<BackendSettings.Empty> backend) {
        log.debug("[DebugConnector] send event={} to backend with destination={}", event, backend.destination());
        return Mono.empty();
    }
}
