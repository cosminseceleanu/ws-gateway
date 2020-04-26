package com.cosmin.wsgateway.application.gateway.connector;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DebugConnector implements BackendConnector<BackendSettings.Empty> {
    @Override
    public boolean supports(Backend.Type type) {
        return true;
    }

    @Override
    public Result sendEvent(Event event, Backend<BackendSettings.Empty> backend) {
        log.info("send event={} to backendType={} with destination={}", event, backend.type(), backend.destination());
        return Result.ofSuccessful(backend, event);
    }
}
