package com.cosmin.wsgateway.domain;

import com.cosmin.wsgateway.domain.exceptions.InvalidRouteException;
import com.cosmin.wsgateway.domain.validation.constraints.ExpressionByRouteType;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

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
    @NotNull
    @EqualsAndHashCode.Exclude
    @With
    private final Set<Backend<? extends BackendSettings>> backends;

    @EqualsAndHashCode.Exclude
    @With
    @Valid
    private final Optional<Expression<Boolean>> expression;

    public boolean appliesTo(String json) {
        if (type != Type.CUSTOM) {
            throw new InvalidRouteException("Expression can be evaluated only for custom routes");
        }
        if (expression.isEmpty()) {
            throw new InvalidRouteException("Custom route must have an expression");
        }

        return expression.get().evaluate(json);
    }

    public boolean hasExpression() {
        return expression != null && expression.isPresent();
    }

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
        return custom("Custom", Collections.emptySet(), expression);
    }

    public static Route custom(String name, Expression<Boolean> expression) {
        return custom(name, Collections.emptySet(), expression);
    }

    public static Route custom(
            String name, Set<Backend<? extends BackendSettings>> backends, Expression<Boolean> expression
    ) {
        return Route.builder()
                .type(Type.CUSTOM)
                .name(name)
                .backends(backends)
                .expression(Optional.ofNullable(expression))
                .build();
    }
}
