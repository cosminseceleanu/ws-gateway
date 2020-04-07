package com.cosmin.wsgateway.domain.model.expressions;

import com.cosmin.wsgateway.domain.model.Expression;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
abstract class BooleanExpression implements Expression<Boolean> {
    @NotNull
    private final Expression<Boolean> left;

    @NotNull
    private final Expression<Boolean> right;

    Expression<Boolean> left() {
        return left;
    }

    Expression<Boolean> right() {
        return right;
    }
}
