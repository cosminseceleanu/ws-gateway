package com.cosmin.wsgateway.domain.expressions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Matches extends TerminalExpression<Boolean, String> {
    public Matches(String path, String value) {
        super(path, value);
    }

    @Override
    public Name name() {
        return Name.MATCHES;
    }

    @Override
    public Boolean evaluate(String json) {
        return getValueForPath(json).matches(value());
    }
}
