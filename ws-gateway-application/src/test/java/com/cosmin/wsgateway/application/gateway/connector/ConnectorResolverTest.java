package com.cosmin.wsgateway.application.gateway.connector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import com.cosmin.wsgateway.domain.exceptions.BackendNotSupportedException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ConnectorResolverTest {

    @Test
    public void testGetConnector() {
        var connectors = List.of(
                createBackendConnectorMock(Backend.Type.HTTP),
                createBackendConnectorMock(Backend.Type.KAFKA)
        );
        var subject = new ConnectorResolver(connectors);
        var result = subject.getConnector(HttpBackend.builder().build());

        assertTrue(result.supports(Backend.Type.HTTP));
    }

    private BackendConnector<? extends BackendSettings> createBackendConnectorMock(Backend.Type backendType) {
        var backend = Mockito.mock(BackendConnector.class);
        doReturn(true).when(backend).supports(backendType);
        return backend;
    }

    @Test
    public void testGetConnector_whenNotSupported_thenThrowsException() {
        var subject = new ConnectorResolver(Collections.emptyList());

        assertThrows(BackendNotSupportedException.class, () -> {
            subject.getConnector(KafkaBackend.builder().build());
        });
    }
}