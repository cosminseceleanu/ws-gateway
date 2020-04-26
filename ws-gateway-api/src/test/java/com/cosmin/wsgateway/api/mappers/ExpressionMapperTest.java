package com.cosmin.wsgateway.api.mappers;

import static com.cosmin.wsgateway.api.fixtures.ExpressionFixtures.createBooleanExpression;
import static com.cosmin.wsgateway.api.fixtures.ExpressionFixtures.createTerminalExpression;
import static com.cosmin.wsgateway.domain.Expression.Name.*;
import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.exceptions.IncorrectExpressionException;
import com.cosmin.wsgateway.domain.Expression;
import com.cosmin.wsgateway.domain.expressions.Expressions;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ExpressionMapperTest {

    private ExpressionMapper subject = new ExpressionMapper(new ObjectMapper());

    @Test
    public void testToModel_whenExpressionIsSimple() {
        Expression<Boolean> result = subject.toModel(createTerminalExpression("$.b", 8.0, EQUAL.getValue()));
        Expression<Boolean> expected = Expressions.equal(8.0, "$.b");

        assertEquals(expected, result);
    }

    @Test
    public void testToModel_whenExpressionIsNotSupported_thenExceptionIsThrown() {
        IncorrectExpressionException thrown = assertThrows(IncorrectExpressionException.class, () -> {
            subject.toModel(createTerminalExpression("$.b", 8.0, "notFound"));
        });
        assertTrue(thrown.getMessage().contains("Expression notFound is not defined"));
    }

    @Test
    public void testToModel_whenExpressionIsNested() {
        Map<String, Object> or = createBooleanExpression(OR.getValue(),
                createTerminalExpression("$.b", 8.0, EQUAL.getValue()),
                createTerminalExpression("$.a", "a", EQUAL.getValue())
        );
        Map<String, Object> root = createBooleanExpression(
                AND.getValue(),
                createTerminalExpression("$.c", "*", MATCHES.getValue()),
                or
        );


        Expression<Boolean> result = subject.toModel(root);
        Expression<Boolean> expected = Expressions.and(
                Expressions.matches("*", "$.c"),
                Expressions.or(
                        Expressions.equal(8.0, "$.b"),
                        Expressions.equal("a", "$.a")
                )
        );

        assertEquals(expected, result);
    }

    @Test
    public void testToModel_whenNestedLevelIsGreaterThan2_thenExceptionIsThrown() {
        Map<String, Object> expr1 = createTerminalExpression("$.b", 8.0, EQUAL.getValue());
        Map<String, Object> expr2 = createTerminalExpression("$.a", "a", EQUAL.getValue());
        Map<String, Object> and = createBooleanExpression(AND.getValue(), expr1, expr2);
        Map<String, Object> or1 = createBooleanExpression(OR.getValue(), and, expr2);
        Map<String, Object> root = createBooleanExpression(OR.getValue(), or1, expr1);

        IncorrectExpressionException thrown = assertThrows(IncorrectExpressionException.class, () -> {
            subject.toModel(root);
        });
        assertTrue(thrown.getMessage().contains("Expression can at most 2 nested levels"));
    }

    @Test
    public void testToModel_whenRootHasMoreThanOneElement_thenExceptionIsThrown() {
        Map<String, Object> expr1 = createTerminalExpression("$.b", 8.0, EQUAL.getValue());
        Map<String, Object> expr2 = createTerminalExpression("$.a", "a", EQUAL.getValue());
        Map<String, Object> root = new HashMap<>(createBooleanExpression(AND.getValue(), expr1, expr2));
        root.put(EQUAL.getValue(), expr1.get(EQUAL.getValue()));

        IncorrectExpressionException thrown = assertThrows(IncorrectExpressionException.class, () -> {
            subject.toModel(root);
        });
        assertTrue(thrown.getMessage().contains("Expression can have only one root"));
    }

    @Test
    public void testToModel_whenBooleanExpressionHasMoreThanTwoChilds_thenExceptionIsThrown() {
        Map<String, Object> expr = createTerminalExpression("$.b", 8.0, EQUAL.getValue());
        Map<String, Object> root = Map.of(AND.getValue(), List.of(expr, expr, expr));

        IncorrectExpressionException thrown = assertThrows(IncorrectExpressionException.class, () -> {
            subject.toModel(root);
        });
        assertTrue(thrown.getMessage().contains("Boolean expression and must have exactly 2"));
    }

    @Test
    public void testToModel_whenBooleanExpressionHasOneChild_thenExceptionIsThrown() {
        Map<String, Object> expr = createTerminalExpression("$.b", 8.0, EQUAL.getValue());
        Map<String, Object> root = Map.of(AND.getValue(), List.of(expr));

        IncorrectExpressionException thrown = assertThrows(IncorrectExpressionException.class, () -> {
            subject.toModel(root);
        });
        assertTrue(thrown.getMessage().contains("Boolean expression and must have exactly 2"));
    }

    @Test
    public void testToModel_whenBooleanExpressionHasChildThatIsNotObject_thenExceptionIsThrown() {
        Map<String, Object> expr = createTerminalExpression("$.b", 8.0, EQUAL.getValue());
        Map<String, Object> root = Map.of(AND.getValue(), List.of(expr, "something broken"));

        IncorrectExpressionException thrown = assertThrows(IncorrectExpressionException.class, () -> {
            subject.toModel(root);
        });
        assertTrue(thrown.getMessage().contains("child as json objects"));
    }

    @Test
    public void testToModel_whenBooleanExpressionIsArray_thenExceptionIsThrown() {
        Map<String, Object> expr = createTerminalExpression("$.b", 8.0, EQUAL.getValue());
        Map<String, Object> root = Map.of(AND.getValue(), expr);

        IncorrectExpressionException thrown = assertThrows(IncorrectExpressionException.class, () -> {
            subject.toModel(root);
        });
        assertTrue(thrown.getMessage().contains("Boolean expression and must be array"));
    }

    @Test
    public void testToRepresentation_whenBooleanExpression() {
        Expression<Boolean> initial = Expressions.and(
                Expressions.matches("*", "$.c"),
                Expressions.or(
                        Expressions.equal(8.0, "$.b"),
                        Expressions.equal("a", "$.a")
                )
        );

        Map<String, Object> expected = createBooleanExpression(
                AND.getValue(),
                createTerminalExpression("$.c", "*", MATCHES.getValue()),
                createBooleanExpression(OR.getValue(),
                        createTerminalExpression("$.b", 8.0, EQUAL.getValue()),
                        createTerminalExpression("$.a", "a", EQUAL.getValue())
                )
        );
        Map<String, Object> result = subject.toRepresentation(initial);

        assertEquals(expected, result);
    }
}