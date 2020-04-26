package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.fixtures.BackendFixtures;
import com.cosmin.wsgateway.api.representation.BackendRepresentation;
import com.cosmin.wsgateway.api.representation.KafkaBackendRepresentation;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import org.junit.jupiter.api.Test;

import static com.cosmin.wsgateway.api.fixtures.BackendFixtures.ADDITIONAL_HEADERS;
import static com.cosmin.wsgateway.api.fixtures.BackendFixtures.HTTP_DESTINATION;
import static org.junit.jupiter.api.Assertions.*;

class BackendMapperTest {

    private BackendMapper subject = new BackendMapper();

    @Test
    public void testToModel_httpBackend() {
        Backend<? extends BackendSettings> backend = subject.toModel(BackendFixtures.defaultHttpRepresentation());

        assertTrue(backend instanceof HttpBackend);
        HttpBackend httpBackend = (HttpBackend) backend;
        assertNotNull(backend);
        assertEquals(HTTP_DESTINATION, httpBackend.getDestination());
        assertNotNull(backend.settings());
        assertEquals(120, httpBackend.getSettings().getTimeoutInMillis());
        assertEquals(ADDITIONAL_HEADERS, httpBackend.getSettings().getAdditionalHeaders());
    }

    @Test
    public void testToRepresentation_httpBackend() {
        HttpBackend model = HttpBackend.builder()
                .destination(HTTP_DESTINATION)
                .settings(HttpSettings.builder()
                        .timeoutInMillis(120)
                        .additionalHeaders(ADDITIONAL_HEADERS)
                        .build())
                .build();

        BackendRepresentation result = subject.toRepresentation(model);

        assertEquals(BackendFixtures.defaultHttpRepresentation(), result);
    }

    @Test
    public void testToRepresentation_kafkaBackend() {
        KafkaBackend model = KafkaBackend.builder()
                .topic("topic")
                .build();
        KafkaBackendRepresentation expected = KafkaBackendRepresentation.builder().topic("topic").build();
        BackendRepresentation result = subject.toRepresentation(model);

        assertEquals(expected, result);
    }

    @Test
    public void testToModel_kafkaBackend() {
        KafkaBackend expected = KafkaBackend.builder()
                .topic("topic")
                .build();
        KafkaBackendRepresentation initial = KafkaBackendRepresentation.builder().topic("topic").build();
        Backend<? extends BackendSettings> result = subject.toModel(initial);

        assertEquals(expected, result);
    }
}
