package com.cosmin.wsgateway.domain.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cosmin.wsgateway.domain.Expression;
import com.cosmin.wsgateway.domain.Fixtures;
import org.junit.jupiter.api.Test;

class OrTest {
    @Test
    public void testName() {
        var subject = Expressions.or(null, null);

        assertEquals(Expression.Name.OR, subject.name());
    }

    @Test
    public void testEvaluate_whenBothChildExpressionsDidNotPassed_thenReturnFalse() {
        Expression<Boolean> equal = Expressions.matches("regex", "$.string");
        var subject = Expressions.or(equal, equal);

        assertFalse(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenOneChildExpressionsPassed_thenReturnTrue() {
        Expression<Boolean> equal1 = Expressions.equal(123, "$.number");
        Expression<Boolean> equal2 = Expressions.equal(2, "$.number");
        var subject = Expressions.or(equal1, equal2);

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenIsNested_thenIsEvaluated() {
        Expression<Boolean> equal1 = Expressions.equal(123, "$.number");
        Expression<Boolean> equal2 = Expressions.equal(2, "$.number");
        var subject = Expressions.or(equal1, Expressions.or(equal1, equal2));

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }
}