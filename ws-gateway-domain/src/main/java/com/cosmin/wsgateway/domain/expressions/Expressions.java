package com.cosmin.wsgateway.domain.expressions;

import com.cosmin.wsgateway.domain.Expression;

public class Expressions {
    public static Expression<Boolean> and(Expression<Boolean> left, Expression<Boolean> right) {
        return new And(left, right);
    }

    public static Expression<Boolean> or(Expression<Boolean> left, Expression<Boolean> right) {
        return new Or(left, right);
    }

    public static Expression<Boolean> matches(String regex, String jsonPath) {
        return new Matches(jsonPath, regex);
    }

    public static <R> Expression<Boolean> equal(R value, String jsonPath) {
        return new Equal<>(jsonPath, value);
    }
}
