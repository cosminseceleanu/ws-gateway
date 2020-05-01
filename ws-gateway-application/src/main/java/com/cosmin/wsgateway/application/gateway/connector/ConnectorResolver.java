package com.cosmin.wsgateway.application.gateway.connector;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.exceptions.BackendNotSupportedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ConnectorResolver {
    private final List<BackendConnector<? extends BackendSettings>> connectors;

    public BackendConnector<? extends BackendSettings> getConnector(Backend<? extends BackendSettings> backend) {
        return connectors.stream()
                .filter(c -> c.supports(backend.type()))
                .findFirst()
                .orElseThrow(() -> new BackendNotSupportedException(backend.type().toString()));
    }
}
