package com.cosmin.wsgateway.domain;

import com.cosmin.wsgateway.domain.exceptions.RouteNotFoundException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

class EndpointTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testEqualsAndHashCode() {
        var e1 = Fixtures.defaultEndpoint();
        var e2 = Fixtures.defaultEndpoint();
        var e3 = Fixtures.defaultEndpoint().withPath("/aaa");

        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }

    @Test
    public void testInvalid_whenPathIsNull() {
        var subject = Fixtures.defaultEndpoint();
        subject = subject.withPath(null);

        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<Endpoint> violation = constraints.iterator().next();
        assertEquals("path", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenPathMatchesInternalPath() {
        var subject = Fixtures.defaultEndpoint();
        subject = subject.withPath("/api/internal/test");

        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<Endpoint> violation = constraints.iterator().next();
        assertEquals("path", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("must match"));
    }

    @Test
    public void testInvalid_whenConfigurationIsNull() {
        var subject = Fixtures.defaultEndpoint();
        subject = subject.withConfiguration(null);

        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<Endpoint> violation = constraints.iterator().next();
        assertEquals("configuration", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenConfigurationIsNotValidNull() {
        var subject = Fixtures.defaultEndpoint();
        subject = subject.withConfiguration(EndpointConfiguration.builder().build());

        var constraints = validator.validate(subject);

        assertFalse(constraints.isEmpty());
    }

    @Test
    public void testImmutable_whenRoutesAreChanged_thenExceptionIsThrown() {
        Endpoint endpoint = Endpoint.builder()
                .configuration(EndpointConfiguration.builder().routes(new HashSet<>()).build())
                .build();

        assertThrows(UnsupportedOperationException.class, () -> {
            endpoint.getRoutes().add(Route.connect());
        });
    }

    @Test
    public void testGetConnectRoute() {
        var subject = Fixtures.defaultEndpoint();

        assertEquals(Fixtures.getConnectRoute(), subject.getConnectRoute());
    }

    @Test
    public void testGetConnectRoute_whenRouteNotExists_thenExceptionIsThrown() {
        var initial = Fixtures.defaultEndpoint();
        var subject = initial.withConfiguration(initial.getConfiguration().withRoutes(Collections.emptySet()));

        assertThrows(RouteNotFoundException.class, subject::getConnectRoute);
    }

    @Test
    public void testGetDisconnectRoute() {
        var subject = Fixtures.defaultEndpoint();

        assertEquals(Fixtures.getDisconnectRoute(), subject.getDisconnectRoute());
    }

    @Test
    public void testGetDefaultRoute() {
        var subject = Fixtures.defaultEndpoint();

        assertEquals(Fixtures.getDefaultRoute(), subject.getDefaultRoute());
    }

    @Test
    public void testHasRoute() {
        var subject = Fixtures.defaultEndpoint();

        assertTrue(subject.hasRoute(Route.Type.CONNECT));
        assertFalse(subject.hasRoute(Route.Type.CUSTOM));
    }

    @Test
    public void testAddRoutes() {
        var subject = Fixtures.defaultEndpoint();
        Route newRoute = Route.custom(null);
        var updated = subject.addRoutes(Set.of(newRoute));

        assertTrue(updated.getRoutes().contains(newRoute));
        assertNotSame(subject, updated);
    }

    @ParameterizedTest
    @MethodSource("pathsAndResults")
    public void testMatchesPath(String endpointPath, String targetPath, boolean expectedResult) {
        var subject = Fixtures.defaultEndpoint().withPath(endpointPath);

        assertEquals(expectedResult, subject.matchesPath(targetPath));
    }

    private static Stream<Arguments> pathsAndResults() {
        return Stream.of(
                Arguments.of("/test", "/test/test1", false),
                Arguments.of("/test", "/test", true),
                Arguments.of("^/any(.*)", "/any/path/to/something", true)
        );
    }
}
