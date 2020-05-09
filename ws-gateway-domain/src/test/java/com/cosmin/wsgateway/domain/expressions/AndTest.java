package com.cosmin.wsgateway.domain.expressions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.Expression;
import com.cosmin.wsgateway.domain.Fixtures;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AndTest {
    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testName() {
        var subject = Expressions.and(null, null);

        assertEquals(Expression.Name.AND, subject.name());
    }

    @Test
    public void testInvalid_whenChildExpressionIsNull() {
        var subject = Expressions.and(null, null);
        var constraints = validator.validate(subject);

        assertEquals(2, constraints.size());
        ConstraintViolation<Expression<Boolean>> violation = constraints.iterator().next();
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testEvaluate_whenBothChildExpressionsPassed_thenReturnTrue() {
        Expression<Boolean> equal = Expressions.equal(123, "$.number");
        var subject = Expressions.and(equal, equal);

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenOneChildExpressionsDidNotPassed_thenReturnFalse() {
        Expression<Boolean> equal1 = Expressions.equal(123, "$.number");
        Expression<Boolean> equal2 = Expressions.equal(2, "$.number");
        var subject = Expressions.and(equal1, equal2);

        assertFalse(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenIsNested_thenIsEvaluated() {
        Expression<Boolean> equal1 = Expressions.equal(123, "$.number");
        Expression<Boolean> equal2 = Expressions.equal(2, "$.number");
        var subject = Expressions.and(equal1, Expressions.or(equal1, equal2));

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }
}