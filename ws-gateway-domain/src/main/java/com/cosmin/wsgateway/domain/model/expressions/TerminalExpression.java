package com.cosmin.wsgateway.domain.model.expressions;

import com.cosmin.wsgateway.domain.model.Expression;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
abstract class TerminalExpression<T, R> implements Expression<T> {
    @NotNull
    private final String path;

    @NotNull
    private final R value;

    public String path() {
        return path;
    }

    public R value() {
        return value;
    }

    public R getValueForPath(String json) {
        return JsonPath.read(json, path);
    }
}
