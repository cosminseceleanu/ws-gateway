package com.cosmin.wsgateway.domain.model.expressions;


public class Matches extends TerminalExpression<Boolean, String> {
    public Matches(String path, String value) {
        super(path, value);
    }

    @Override
    public Name name() {
        return Name.EQUAL;
    }

    @Override
    public Boolean evaluate(String json) {
        return getValueForPath(json).matches(value());
    }
}
