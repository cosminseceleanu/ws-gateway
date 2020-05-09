package com.cosmin.wsgateway.domain.validation.validators;

import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.validation.constraints.ValidRouteConfiguration;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidRouteConfigurationValidator implements
        ConstraintValidator<ValidRouteConfiguration, EndpointConfiguration> {

    @Override
    public boolean isValid(EndpointConfiguration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.getRoutes() == null) {
            return true;
        }

        return checkRouteNumberByTypes(context, value, Route.Type.CONNECT)
                && checkRouteNumberByTypes(context, value, Route.Type.DEFAULT)
                && checkRouteNumberByTypes(context, value, Route.Type.DISCONNECT);
    }

    private Boolean checkRouteNumberByTypes(
            ConstraintValidatorContext context, EndpointConfiguration configuration, Route.Type type
    ) {
        Set<Route> routes = configuration.getRoutesByType(type);
        if (routes.size() > 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Endpoint can have at most 1 route of type " + type)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
