package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.fixtures.BackendFixtures;
import com.cosmin.wsgateway.api.fixtures.RouteFixtures;
import com.cosmin.wsgateway.api.representation.RouteRepresentation;
import com.cosmin.wsgateway.domain.Expression;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.expressions.Expressions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RouteMapperTest {
    @Mock
    private ExpressionMapper expressionMapper;

    @Mock
    private BackendMapper backendMapper;

    @InjectMocks
    private RouteMapper subject;

    private final Expression<Boolean> defaultExpression = Expressions.matches("*", "$.c");

    @Test
    public void testToModel() {
        RouteRepresentation initial = RouteFixtures.connectRepresentation();
        doReturn(Set.of(BackendFixtures.defaultHttpBackend())).when(backendMapper).toModels(any());

        Route result = subject.toModel(initial);

        assertEquals(Route.Type.CONNECT, result.getType());
        assertEquals("Connect", result.getName());
        assertThat(result.getBackends(), contains(BackendFixtures.defaultHttpBackend()));
    }

    @Test
    public void testToModel_withExpression() {
        RouteRepresentation initial = RouteFixtures.customRouteRepresentation();
        doReturn(Set.of(BackendFixtures.defaultHttpBackend())).when(backendMapper).toModels(any());
        doReturn(defaultExpression).when(expressionMapper).toModel(any());

        Route result = subject.toModel(initial);

        assertEquals(Route.Type.CUSTOM, result.getType());
        assertEquals(defaultExpression, result.getExpression().get());
    }

    @Test
    public void testToRepresentation() {
        Route route = Route.connect(Set.of(BackendFixtures.defaultHttpBackend()));
        doReturn(Set.of(BackendFixtures.defaultHttpRepresentation())).when(backendMapper).toRepresentations(any());

        RouteRepresentation result = subject.toRepresentation(route);

        assertEquals(RouteRepresentation.Type.CONNECT, result.getType());
        assertEquals("Connect", result.getName());
        assertThat(result.getBackends(), contains(BackendFixtures.defaultHttpRepresentation()));
        assertNull(result.getExpression());
    }

    @Test
    public void testToRepresentation_withExpression() {
        Route route = Route.custom(defaultExpression);
        doReturn(Set.of(BackendFixtures.defaultHttpRepresentation())).when(backendMapper).toRepresentations(any());
        doReturn(Map.of("key", "value")).when(expressionMapper).toRepresentation(defaultExpression);

        RouteRepresentation result = subject.toRepresentation(route);

        assertEquals(RouteRepresentation.Type.CUSTOM, result.getType());
        assertEquals("Custom", result.getName());
        assertThat(result.getBackends(), contains(BackendFixtures.defaultHttpRepresentation()));
        assertEquals(Map.of("key", "value"), result.getExpression());
    }
}