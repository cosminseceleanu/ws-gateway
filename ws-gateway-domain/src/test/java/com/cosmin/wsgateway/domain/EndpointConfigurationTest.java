package com.cosmin.wsgateway.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.expressions.Expressions;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class EndpointConfigurationTest extends BaseTest {
    @Test
    public void testInvalid_whenAuthIsNull() {
        var subject = Fixtures.defaultEndpoint().getConfiguration();
        subject = subject.withAuthentication(null);

        var constraints = validator.validate(subject);

        assertViolationForProperty(constraints, "authentication", "null");
    }

    @Test
    public void testInvalid_whenGeneralSettingsAreNull() {
        var subject = Fixtures.defaultEndpoint().getConfiguration();
        subject = subject.withGeneralSettings(null);

        var constraints = validator.validate(subject);

        assertViolationForProperty(constraints, "generalSettings", "null");
    }

    @Test
    public void testInvalid_whenGeneralSettingsAreInvalid() {
        var subject = Fixtures.defaultEndpoint().getConfiguration();
        subject = subject.withGeneralSettings(GeneralSettings.ofDefaults().toBuilder().backendParallelism(100).build());

        var constraints = validator.validate(subject);

        assertViolationForProperty(constraints, "generalSettings.backendParallelism", "must be less than");
    }

    @Test
    public void testInvalid_whenRoutesAreNull() {
        var subject = Fixtures.defaultEndpoint().getConfiguration();
        subject = subject.withRoutes(null);

        var constraints = validator.validate(subject);

        assertViolationForProperty(constraints, "routes", "null");
    }

    @Test
    public void testInvalid_whenFiltersAreNull() {
        var subject = Fixtures.defaultEndpoint().getConfiguration();
        subject = subject.withFilters(null);

        var constraints = validator.validate(subject);

        assertViolationForProperty(constraints, "filters", "null");
    }

    @Test
    public void testInvalid_whenTwoConnectRoutesArePresent() {
        var subject = Fixtures.defaultEndpoint()
                .addRoutes(Collections.singleton(Route.builder()
                        .name("Connect2")
                        .type(Route.Type.CONNECT)
                        .backends(Collections.emptySet())
                        .build()))
                .getConfiguration();

        var constraints = validator.validate(subject);

        assertViolationForProperty(constraints, "", "CONNECT");
    }

    @Test
    public void testValid_whenContainsTwoCustomRoutes() {
        var subject = Fixtures.defaultEndpoint()
                .addRoutes(Set.of(
                        Route.custom("Custom1", Expressions.equal("a", "b")),
                        Route.custom("Custom2", Expressions.equal("a", "b"))
                ))
                .getConfiguration();

        var constraints = validator.validate(subject);

        assertTrue(constraints.isEmpty());
    }

    private void assertViolationForProperty(Set<ConstraintViolation<EndpointConfiguration>> constraints, String property, String msg) {
        assertEquals(1, constraints.size());
        ConstraintViolation<EndpointConfiguration> violation = constraints.iterator().next();
        assertEquals(property, violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString(msg));
    }
}