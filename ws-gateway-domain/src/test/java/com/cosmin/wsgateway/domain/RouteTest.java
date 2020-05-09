package com.cosmin.wsgateway.domain;

import static com.cosmin.wsgateway.domain.validation.validators.ExpressionByRouteTypeValidator.EXPRESSION_SET_FOR_NON_CUSTOM_ROUTE_MESSAGE;
import static com.cosmin.wsgateway.domain.validation.validators.ExpressionByRouteTypeValidator.MISSING_EXPRESSION_MESSAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.exceptions.InvalidRouteException;
import com.cosmin.wsgateway.domain.expressions.Expressions;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RouteTest {
    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testInvalid_whenTypeIsNull() {
        var route = Route.builder().name("asdfs").backends(Collections.emptySet()).type(null).build();

        var constraints = validator.validate(route);

        assertEquals(1, constraints.size());
        ConstraintViolation<Route> violation = constraints.iterator().next();
        assertEquals("type", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenNameIsNull() {
        var route = Route.builder().name(null).backends(Collections.emptySet()).type(Route.Type.CONNECT).build();

        var constraints = validator.validate(route);

        assertEquals(1, constraints.size());
        ConstraintViolation<Route> violation = constraints.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenNameSizeIsSmallerThanAllowed() {
        var route = Route.builder().name("aa").backends(Collections.emptySet()).type(Route.Type.CONNECT).build();

        var constraints = validator.validate(route);

        assertEquals(1, constraints.size());
        ConstraintViolation<Route> violation = constraints.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("size"));
    }

    @Test
    public void testInvalid_whenNameSizeIsGreaterThanAllowed() {
        var name = IntStream.rangeClosed(1, 300).mapToObj(Integer::toString).collect(Collectors.joining(","));
        var route = Route.builder().name(name).backends(Collections.emptySet()).type(Route.Type.CONNECT).build();

        var constraints = validator.validate(route);

        assertEquals(1, constraints.size());
        ConstraintViolation<Route> violation = constraints.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("size"));
    }

    @Test
    public void testInvalid_whenExpressionIsSetToNonCustomType() {
        var route = Route.connect().withExpression(Optional.of(Expressions.equal("aa", "bbb")));

        var constraints = validator.validate(route);

        assertEquals(1, constraints.size());
        assertEquals(EXPRESSION_SET_FOR_NON_CUSTOM_ROUTE_MESSAGE, constraints.iterator().next().getMessage());
    }

    @Test
    public void testInvalid_whenExpressionIsEmptyForCustomType() {
        var route = Route.custom(null);

        var constraints = validator.validate(route);

        assertEquals(1, constraints.size());
        assertEquals(MISSING_EXPRESSION_MESSAGE, constraints.iterator().next().getMessage());
    }

    @Test
    public void testInvalid_whenBackendIsNotValid() {
        var route = Route.connect().withBackends(Set.of(HttpBackend.builder().build()));

        var constraints = validator.validate(route);

        assertFalse(constraints.isEmpty());
    }

    @Test
    public void testInvalid_whenBackendsAreNull() {
        var route = Route.connect().withBackends(null);

        var constraints = validator.validate(route);

        assertEquals(1, constraints.size());
        ConstraintViolation<Route> violation = constraints.iterator().next();
        assertEquals("backends", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenExpressionIsInvalid() {
        var route = Route.custom(Expressions.equal(null, null));

        var constraints = validator.validate(route);

        assertFalse(constraints.isEmpty());
    }

    @Test
    public void testValid() {
        var route = Route.connect();

        var constraints = validator.validate(route);

        assertEquals(0, constraints.size());
    }

    @Test
    public void testEqualsAndHashCode() {
        var r1 = Route.connect();
        var r2 = Route.connect(Collections.singleton(HttpBackend.builder().build()));
        var r3 = Route.connect().withExpression(Optional.of(Expressions.equal("a", "b")));

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertEquals(r2, r3);
        assertEquals(r2, r3);
    }

    @Test
    public void testImmutable_whenBackendsAreChanged_thenExceptionIsThrown() {
        var route = Route.connect();

        assertThrows(UnsupportedOperationException.class, () -> {
            route.getBackends().add(HttpBackend.builder().build());
        });
    }

    @Test
    public void testAppliesTo_whenIsNotCustom_thenExceptionIsThrown() {
        var route = Route.connect();

        assertThrows(InvalidRouteException.class, () -> {
            route.appliesTo("{}");
        });
    }

    @Test
    public void testAppliesTo_whenExpressionIsMissing_thenExceptionIsThrown() {
        var route = Route.custom(null);

        assertThrows(InvalidRouteException.class, () -> {
            route.appliesTo("{}");
        });
    }

    @Test
    public void testAppliesTo_thenExpressionIsEvaluated() {
        var expression = mock(Expression.class);
        doReturn(true).when(expression).evaluate("{}");
        var route = Route.custom(expression);

        assertTrue(route.appliesTo("{}"));
    }
}