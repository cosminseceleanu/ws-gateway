package com.cosmin.wsgateway.domain.expressions;

import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.Expression;
import com.cosmin.wsgateway.domain.Fixtures;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MatchesTest {

    @Test
    public void testName() {
        var subject = Expressions.matches("*", "*");

        assertEquals(Expression.Name.MATCHES, subject.name());
    }

    @ParameterizedTest
    @MethodSource("regexAndResults")
    public void testEvaluate(String regex, boolean expectedResult) {
        var subject = Expressions.matches(regex, "$.string");

        assertEquals(expectedResult, subject.evaluate(Fixtures.sampleJson()));
    }

    private static Stream<Arguments> regexAndResults() {
        return Stream.of(
           Arguments.of("", false),
           Arguments.of("^Hello(.*)", true),
           Arguments.of("Hello World", true)
        );
    }

    @Test
    public void testEvaluate_whenJsonPathDoesNotExists_thenReturnFalse() {
        var subject = Expressions.matches("any", "$.bar");

        assertFalse(subject.evaluate("{}"));
    }
}