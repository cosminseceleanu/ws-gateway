package com.cosmin.wsgateway.domain.expressions;

import com.cosmin.wsgateway.domain.Expression;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.Optional;
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

    protected Optional<R> getValueForPath(String json) {
        try {
            return Optional.ofNullable(JsonPath.read(json, path));
        }  catch (PathNotFoundException e) {
            return Optional.empty();
        }
    }
}
