package com.cosmin.wsgateway.domain.expressions;


import com.cosmin.wsgateway.domain.Expression;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class And extends BooleanExpression {
    public And(Expression<Boolean> left, Expression<Boolean> right) {
        super(left, right);
    }

    @Override
    public Name name() {
        return Name.AND;
    }

    @Override
    public Boolean evaluate(String json) {
        return left().evaluate(json) && right().evaluate(json);
    }
}
