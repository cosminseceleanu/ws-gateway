package com.cosmin.wsgateway.api.rest.mappers;

import com.cosmin.wsgateway.api.BackendFixtures;
import com.cosmin.wsgateway.api.rest.representation.HttpBackendRepresentation;
import com.cosmin.wsgateway.domain.model.backends.HttpBackend;
import com.cosmin.wsgateway.domain.model.backends.HttpSettings;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpBackendMapperTest {

    private HttpBackendMapper subject = new HttpBackendMapper();

    @Test
    public void testToModel() {
        HttpBackend backend = subject.toModel(BackendFixtures.defaultHttpRepresentation());

        assertNotNull(backend);
        assertEquals("aaa", backend.getDestination());
        assertNotNull(backend.settings());
        assertEquals(120, backend.getSettings().getTimeoutInMillis());
        assertEquals(Map.of("key", "value"), backend.getSettings().getAdditionalHeaders());
    }

    @Test
    public void testToRepresentation() {
        HttpBackend model = HttpBackend.builder()
                .destination("aaa")
                .settings(HttpSettings.builder()
                        .timeoutInMillis(120)
                        .additionalHeaders(Map.of("key", "value"))
                        .build())
                .build();

        HttpBackendRepresentation result = subject.toRepresentation(model);

        assertEquals(BackendFixtures.defaultHttpRepresentation(), result);
    }
}
