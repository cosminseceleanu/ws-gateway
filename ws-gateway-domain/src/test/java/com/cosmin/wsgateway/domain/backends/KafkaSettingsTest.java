package com.cosmin.wsgateway.domain.backends;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.BaseTest;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class KafkaSettingsTest extends BaseTest {

    @Test
    public void testInvalid_whenAcksIsNull() {
        var subject = KafkaSettings.builder()
                .retriesNr(1)
                .bootstrapServers("assssss")
                .build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<KafkaSettings> violation = constraints.iterator().next();
        assertEquals("acks", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenRetriesExceedMaxAllowed() {
        var subject = KafkaSettings.builder()
                .retriesNr(111)
                .acks(KafkaSettings.Ack.LEADER)
                .bootstrapServers("assssss")
                .build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<KafkaSettings> violation = constraints.iterator().next();
        assertEquals("retriesNr", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("must be less"));
    }
}