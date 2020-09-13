package com.cosmin.wsgateway.api.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.api.representation.GeneralSettingsRepresentation;
import com.cosmin.wsgateway.domain.GeneralSettings;
import org.junit.jupiter.api.Test;

class GeneralSettingsMapperTest {
    private GeneralSettingsMapper subject = new GeneralSettingsMapper();

    @Test
    public void testToModel_goodRepresentation_shouldReturnCorrectModel() {
        var representation = GeneralSettingsRepresentation.builder()
                .backendParallelism(1)
                .heartbeatIntervalInSeconds(10)
                .heartbeatMaxMissingPingFrames(2)
                .build();
        var result = subject.toModel(representation);
        var expected = GeneralSettings.builder()
                .backendParallelism(1)
                .heartbeatIntervalInSeconds(10)
                .heartbeatMaxMissingPingFrames(2)
                .build();

        assertEquals(expected, result);
    }

    @Test
    public void testToModel_nullRepresentation_shouldReturnDefaultModel() {
        var result = subject.toModel(null);

        var expected = GeneralSettings.defaultSettings();

        assertEquals(expected, result);
    }

    @Test
    public void testToRepresentation_defaultModel_shouldReturnRepresentation() {
        var result = subject.toRepresentation(GeneralSettings.defaultSettings());

        var expected = GeneralSettingsRepresentation.builder()
                .backendParallelism(GeneralSettingsRepresentation.DEFAULT_BACKEND_PARALLELISM)
                .heartbeatIntervalInSeconds(GeneralSettingsRepresentation.DEFAULT_HEARTBEAT_INTERVAL)
                .heartbeatMaxMissingPingFrames(GeneralSettingsRepresentation.DEFAULT_MISSING_PING_FRAMES)
                .build();

        assertEquals(expected, result);
    }
}