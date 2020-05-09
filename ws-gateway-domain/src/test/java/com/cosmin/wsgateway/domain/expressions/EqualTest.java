package com.cosmin.wsgateway.domain.expressions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cosmin.wsgateway.domain.Expression;
import com.cosmin.wsgateway.domain.Fixtures;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EqualTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testName() {
        var subject = Expressions.equal("Hello World", "$.string");

        assertEquals(Expression.Name.EQUAL, subject.name());
    }

    @Test
    public void testEvaluate_whenValueIsString() {
        var subject = Expressions.equal("Hello World", "$.string");

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenNotEquals_thenResultIsFalse() {
        var subject = Expressions.equal("aaaa", "$.string");

        assertFalse(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenValueIsNumber() {
        var subject = Expressions.equal(123, "$.number");

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenValueIsArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.appendElement(1);
        jsonArray.appendElement(2);
        jsonArray.appendElement(3);
        var subject = Expressions.equal(jsonArray, "$.array");

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenValueIsBoolean() {
        var subject = Expressions.equal(true, "$.boolean");

        assertTrue(subject.evaluate(Fixtures.sampleJson()));
    }

    @Test
    public void testEvaluate_whenJsonPathDoesNotExists_thenReturnFalse() {
        var subject = Expressions.equal("asda", "$.bar");

        assertFalse(subject.evaluate("{}"));
    }

    @Test
    public void testInvalid_whenValueIsNull() {
        var subject = Expressions.equal(null, "a");
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<Expression<Boolean>> violation = constraints.iterator().next();
        assertEquals("value", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }

    @Test
    public void testInvalid_whenPathIsNull() {
        var subject = Expressions.equal(2, null);
        var constraints = validator.validate(subject);

        assertEquals(1, constraints.size());
        ConstraintViolation<Expression<Boolean>> violation = constraints.iterator().next();
        assertEquals("path", violation.getPropertyPath().toString());
        assertThat(violation.getMessage(), containsString("null"));
    }
}
