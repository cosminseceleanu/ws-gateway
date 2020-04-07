package com.cosmin.wsgateway.domain.model;

import com.cosmin.wsgateway.domain.validation.constraints.ExpressionByRouteType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(4)
    @Max(255)
    private final String name;

    @NotNull
    private final Type type;

    @Size(max = 10)
    @Valid
    @EqualsAndHashCode.Exclude
    private final Set<Backend<BackendSettings>> backends;

    @EqualsAndHashCode.Exclude
    private final Optional<Expression<Boolean>> expression;

    public static Route connect() {
        return connect(Collections.emptySet());
    }

    public static Route connect(Set<Backend<BackendSettings>> backends) {
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

    public static Route disconnect(Set<Backend<BackendSettings>> backends) {
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

    public static Route defaultRoute(Set<Backend<BackendSettings>> backends) {
        return Route.builder()
                .type(Type.DEFAULT)
                .name("Default route")
                .backends(backends)
                .expression(Optional.empty())
                .build();
    }
}
