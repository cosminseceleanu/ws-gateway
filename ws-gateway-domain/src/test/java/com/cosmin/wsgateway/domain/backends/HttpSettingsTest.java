package com.cosmin.wsgateway.domain.backends;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cosmin.wsgateway.domain.BaseTest;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class HttpSettingsTest extends BaseTest {

    @Test
    public void testInvalid_whenReadTimeoutIsNull() {
        var subject = HttpSettings.builder()
                .connectTimeoutInMillis(233)
                .additionalHeaders(Collections.emptyMap())
                .build();
        var constraints = validator.validate(subject);

        assertConstraintViolation(constraints, "readTimeoutInMillis", "null");
    }

    @Test
    public void testInvalid_whenAdditionalHeadersAreNull() {
        var subject = HttpSettings.builder()
                .connectTimeoutInMillis(233)
                .readTimeoutInMillis(233)
                .build();
        var constraints = validator.validate(subject);

        assertConstraintViolation(constraints, "additionalHeaders", "null");
    }

    @Test
    public void testInvalid_whenReadTimeoutIsLessThanMinAccepted() {
        var subject = HttpSettings.builder()
                .connectTimeoutInMillis(233)
                .readTimeoutInMillis(1)
                .additionalHeaders(Collections.emptyMap())
                .build();
        var constraints = validator.validate(subject);

        assertConstraintViolation(constraints, "readTimeoutInMillis", "must be greater than");
    }

    @Test
    public void testInvalid_whenReadTimeoutIsGreaterThanMaxAccepted() {
        var subject = HttpSettings.builder()
                .connectTimeoutInMillis(100)
                .readTimeoutInMillis(1000000)
                .additionalHeaders(Collections.emptyMap())
                .build();
        var constraints = validator.validate(subject);

        assertConstraintViolation(constraints, "readTimeoutInMillis", "must be less than");
    }

    @Test
    public void testInvalid_whenConnectTimeoutIsLessThanMinAccepted() {
        var subject = HttpSettings.builder()
                .connectTimeoutInMillis(2)
                .readTimeoutInMillis(100)
                .additionalHeaders(Collections.emptyMap())
                .build();
        var constraints = validator.validate(subject);

        assertConstraintViolation(constraints, "connectTimeoutInMillis", "must be greater than");
    }

    @Test
    public void testInvalid_whenConnectTimeoutIsGreaterThanMaxAccepted() {
        var subject = HttpSettings.builder()
                .connectTimeoutInMillis(1001231231)
                .readTimeoutInMillis(200)
                .additionalHeaders(Collections.emptyMap())
                .build();
        var constraints = validator.validate(subject);

        assertConstraintViolation(constraints, "connectTimeoutInMillis", "must be less than");
    }


    private void assertConstraintViolation(Set<ConstraintViolation<HttpSettings>> constraints, String property, String message) {
        assertEquals(1, constraints.size());
        ConstraintViolation<HttpSettings> violation = constraints.iterator().next();
        assertEquals(property, violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString(message));
    }
}