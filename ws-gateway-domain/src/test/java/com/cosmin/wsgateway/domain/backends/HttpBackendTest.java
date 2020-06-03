package com.cosmin.wsgateway.domain.backends;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BaseTest;
import com.cosmin.wsgateway.domain.Fixtures;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class HttpBackendTest extends BaseTest {

    @Test
    public void testInvalid_whenDestinationIsNull() {
        var subject = HttpBackend.builder().destination(null).settings(Fixtures.defaultHttpSettings()).build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<HttpBackend> violation = constraints.iterator().next();
        assertEquals("destination", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenDestinationIsNotUrl() {
        var subject = HttpBackend.builder().destination("something").settings(Fixtures.defaultHttpSettings()).build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<HttpBackend> violation = constraints.iterator().next();
        assertEquals("destination", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("URL"));
    }

    @Test
    public void testInvalid_whenSettingsAreNull() {
        var subject = HttpBackend.builder().destination("http://example.com").settings(null).build();
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<HttpBackend> violation = constraints.iterator().next();
        assertEquals("settings", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenSettingsAreInvalid() {
        var subject = HttpBackend.builder()
                .destination("http://example.com")
                .settings(HttpSettings.builder().readTimeoutInMillis(100).additionalHeaders(null).build())
                .build();
        var constraints = validator.validate(subject);

        assertFalse(constraints.isEmpty());
    }

    @Test
    public void testType() {
        var subject = HttpBackend.builder().build();

        assertEquals(Backend.Type.HTTP, subject.type());
    }
}