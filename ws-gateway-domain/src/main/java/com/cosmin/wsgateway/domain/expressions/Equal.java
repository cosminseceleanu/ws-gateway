package com.cosmin.wsgateway.domain.expressions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Equal<R> extends TerminalExpression<Boolean, R> {
    public Equal(String path, R value) {
        super(path, value);
    }

    @Override
    public Name name() {
        return Name.EQUAL;
    }

    @Override
    public Boolean evaluate(String json) {
        return getValueForPath(json)
                .map(jsonValue -> value().equals(jsonValue))
                .orElse(false);
    }
}
