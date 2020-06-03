package com.cosmin.wsgateway.domain.backends;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.BaseTest;
import com.cosmin.wsgateway.domain.Fixtures;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class KafkaBackendTest extends BaseTest {

    @Test
    public void testInvalid_whenTopicIsNull() {
        var subject = KafkaBackend.builder()
                .settings(Fixtures.defaultKafkaSettings())
                .build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<KafkaBackend> violation = constraints.iterator().next();
        assertEquals("topic", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenTopicSizeIsSmallerThanAccepted() {
        var subject = KafkaBackend.builder()
                .settings(Fixtures.defaultKafkaSettings())
                .topic("abcd")
                .build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<KafkaBackend> violation = constraints.iterator().next();
        assertEquals("topic", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("size"));
    }

    @Test
    public void testInvalid_whenSettingsAreInvalid() {
        var subject = KafkaBackend.builder().topic("abcdfghh").build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<KafkaBackend> violation = constraints.iterator().next();
        assertEquals("settings", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }
}