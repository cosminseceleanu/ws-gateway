package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.fixtures.BackendFixtures;
import com.cosmin.wsgateway.api.representation.BackendRepresentation;
import com.cosmin.wsgateway.api.representation.KafkaBackendRepresentation;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import com.cosmin.wsgateway.domain.backends.KafkaSettings;
import org.junit.jupiter.api.Test;

import static com.cosmin.wsgateway.api.fixtures.BackendFixtures.ADDITIONAL_HEADERS;
import static com.cosmin.wsgateway.api.fixtures.BackendFixtures.HTTP_DESTINATION;
import static org.junit.jupiter.api.Assertions.*;

class BackendMapperTest {

    private final KafkaBackend kafkaModel = KafkaBackend.builder()
            .settings(KafkaSettings.builder()
                    .bootstrapServers("servers")
                    .retriesNr(2)
                    .acks(KafkaSettings.Ack.ALL)
                    .build())
            .topic("topic")
            .build();

    private final KafkaBackendRepresentation kafkaRepresentation = KafkaBackendRepresentation
            .builder()
            .bootstrapServers("servers")
            .topic("topic")
            .acks(KafkaBackendRepresentation.Ack.ALL)
            .retriesNr(2)
            .build();

    private BackendMapper subject = new BackendMapper();

    @Test
    public void testToModel_httpBackendRepresentation_shouldBeMappedToCorrectModel() {
        Backend<? extends BackendSettings> backend = subject.toModel(BackendFixtures.defaultHttpRepresentation());

        assertTrue(backend instanceof HttpBackend);
        HttpBackend httpBackend = (HttpBackend) backend;
        assertNotNull(backend);
        assertEquals(HTTP_DESTINATION, httpBackend.getDestination());
        assertNotNull(backend.settings());
        assertEquals(120, httpBackend.getSettings().getReadTimeoutInMillis());
        assertEquals(ADDITIONAL_HEADERS, httpBackend.getSettings().getAdditionalHeaders());
    }

    @Test
    public void testToRepresentation_httpBackend_shouldBeMappedToCorrectRepresentation() {
        HttpBackend model = HttpBackend.builder()
                .destination(HTTP_DESTINATION)
                .settings(HttpSettings.builder()
                        .readTimeoutInMillis(120)
                        .connectTimeoutInMillis(200)
                        .additionalHeaders(ADDITIONAL_HEADERS)
                        .build())
                .build();

        BackendRepresentation result = subject.toRepresentation(model);

        assertEquals(BackendFixtures.defaultHttpRepresentation(), result);
    }

    @Test
    public void testToRepresentation_kafkaBackend_shouldBeMappedToCorrectRepresentation() {
        BackendRepresentation result = subject.toRepresentation(kafkaModel);

        assertEquals(kafkaRepresentation, result);
    }

    @Test
    public void testToModel_kafkaBackendRepresentation_shouldBeMappedToCorrectModel() {
        Backend<? extends BackendSettings> result = subject.toModel(kafkaRepresentation);

        assertEquals(kafkaModel, result);
    }
}
