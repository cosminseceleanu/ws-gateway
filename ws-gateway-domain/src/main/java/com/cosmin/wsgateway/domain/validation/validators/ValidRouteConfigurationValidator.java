package com.cosmin.wsgateway.domain.validation.validators;

import com.cosmin.wsgateway.domain.model.EndpointConfiguration;
import com.cosmin.wsgateway.domain.model.Route;
import com.cosmin.wsgateway.domain.validation.constraints.ExpressionByRouteType;
import com.cosmin.wsgateway.domain.validation.constraints.ValidRouteConfiguration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

import static com.cosmin.wsgateway.domain.model.Route.Type.*;

public class ValidRouteConfigurationValidator implements ConstraintValidator<ValidRouteConfiguration, EndpointConfiguration> {

    @Override
    public boolean isValid(EndpointConfiguration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return checkRouteNumberByTypes(context, value, CONNECT)
                && checkRouteNumberByTypes(context, value, DEFAULT)
                && checkRouteNumberByTypes(context, value, DISCONNECT);
    }

    private Boolean checkRouteNumberByTypes(ConstraintValidatorContext context, EndpointConfiguration configuration, Route.Type type) {
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
