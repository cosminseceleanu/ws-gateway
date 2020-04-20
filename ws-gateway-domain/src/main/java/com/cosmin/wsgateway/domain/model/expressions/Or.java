package com.cosmin.wsgateway.domain.model.expressions;

import com.cosmin.wsgateway.domain.model.Expression;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Or extends BooleanExpression {
    public Or(Expression<Boolean> left, Expression<Boolean> right) {
        super(left, right);
    }

    @Override
    public Name name() {
        return Name.OR;
    }

    @Override
    public Boolean evaluate(String json) {
        return left().evaluate(json) || right().evaluate(json);
    }
}
