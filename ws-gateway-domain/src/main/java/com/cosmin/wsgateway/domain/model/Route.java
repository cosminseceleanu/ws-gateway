package com.cosmin.wsgateway.domain.model;

import com.cosmin.wsgateway.domain.validation.constraints.ExpressionByRouteType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Value
@Builder
@ExpressionByRouteType
public class Route {
    public enum Type {
        CONNECT,
        DISCONNECT,
        DEFAULT,
        CUSTOM
    }

    @NotNull
    @Size(min = 4, max = 255)
    private final String name;

    @NotNull
    private final Type type;

    @Size(max = 10)
    @Valid
    @EqualsAndHashCode.Exclude
    @With
    private final Set<Backend<? extends BackendSettings>> backends;

    @EqualsAndHashCode.Exclude
    @With
    private final Optional<Expression<Boolean>> expression;

    public static Route connect() {
        return connect(Collections.emptySet());
    }

    public static Route connect(Set<Backend<? extends BackendSettings>> backends) {
        return Route.builder()
                .type(Type.CONNECT)
                .name("Connect")
                .backends(backends)
                .expression(Optional.empty())
                .build();
    }

    public static Route disconnect() {
        return disconnect(Collections.emptySet());
    }

    public static Route disconnect(Set<Backend<? extends BackendSettings>> backends) {
        return Route.builder()
                .type(Type.DISCONNECT)
                .name("Disconnect")
                .backends(backends)
                .expression(Optional.empty())
                .build();
    }

    public static Route defaultRoute() {
        return defaultRoute(Collections.emptySet());
    }

    public static Route defaultRoute(Set<Backend<? extends BackendSettings>> backends) {
        return Route.builder()
                .type(Type.DEFAULT)
                .name("Default")
                .backends(backends)
                .expression(Optional.empty())
                .build();
    }

    public static Route custom(Expression<Boolean> expression) {
        return custom(Collections.emptySet(), expression);
    }

    public static Route custom(Set<Backend<? extends BackendSettings>> backends, Expression<Boolean> expression) {
        return Route.builder()
                .type(Type.CUSTOM)
                .name("Custom")
                .backends(backends)
                .expression(Optional.of(expression))
                .build();
    }
}
