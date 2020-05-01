package com.cosmin.wsgateway.domain.expressions;

import com.cosmin.wsgateway.domain.Expression;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

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
