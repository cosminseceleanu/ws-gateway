package com.cosmin.wsgateway.domain.model.expressions;

import com.cosmin.wsgateway.domain.model.Expression;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class BooleanExpression implements Expression<Boolean> {
    @NotNull
    private final Expression<Boolean> left;

    @NotNull
    private final Expression<Boolean> right;

    public Expression<Boolean> left() {
        return left;
    }

    public Expression<Boolean> right() {
        return right;
    }
}
