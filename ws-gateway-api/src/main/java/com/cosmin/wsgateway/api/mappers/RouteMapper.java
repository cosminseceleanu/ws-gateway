package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.representation.RouteRepresentation;
import com.cosmin.wsgateway.domain.model.Expression;
import com.cosmin.wsgateway.domain.model.Route;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RouteMapper implements RepresentationMapper<RouteRepresentation, Route> {
    private final MapStructMapper selfMapper = Mappers.getMapper(MapStructMapper.class);
    private final ExpressionMapper expressionMapper;
    private final BackendMapper backendMapper;

    @Mapper
    public interface MapStructMapper {
        @Mapping(target = "backends", ignore = true)
        @Mapping(target = "expression", ignore = true)
        Route toRoute(RouteRepresentation representation);

        @Mapping(target = "backends", ignore = true)
        @Mapping(target = "expression", ignore = true)
        RouteRepresentation fromRoute(Route model);
    }

    @Override
    public Route toModel(RouteRepresentation representation) {
        Route route = selfMapper.toRoute(representation);
        Optional<Expression<Boolean>> expression = Optional.ofNullable(representation.getExpression())
                .map(expressionMapper::toModel);

        return route.withBackends(Set.copyOf(backendMapper.toModels(representation.getBackends())))
                .withExpression(expression);
    }

    @Override
    public RouteRepresentation toRepresentation(Route domain) {
        RouteRepresentation representation = selfMapper.fromRoute(domain);
        representation.setBackends(Set.copyOf(backendMapper.toRepresentations(domain.getBackends())));
        representation.setExpression(domain.getExpression()
                .map(expressionMapper::toRepresentation)
                .orElse(null)
        );

        return representation;
    }
}
