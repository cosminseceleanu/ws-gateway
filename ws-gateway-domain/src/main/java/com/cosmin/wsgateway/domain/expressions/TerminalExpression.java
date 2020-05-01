package com.cosmin.wsgateway.domain.expressions;

import com.cosmin.wsgateway.domain.Expression;
import com.jayway.jsonpath.JsonPath;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class TerminalExpression<T, R> implements Expression<T> {
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
