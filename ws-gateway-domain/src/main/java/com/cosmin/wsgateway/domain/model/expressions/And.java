package com.cosmin.wsgateway.domain.model.expressions;


import com.cosmin.wsgateway.domain.model.Expression;

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
