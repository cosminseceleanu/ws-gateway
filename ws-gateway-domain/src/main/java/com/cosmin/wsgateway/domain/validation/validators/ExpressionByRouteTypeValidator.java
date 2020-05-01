package com.cosmin.wsgateway.domain.validation.validators;

import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.validation.constraints.ExpressionByRouteType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExpressionByRouteTypeValidator implements ConstraintValidator<ExpressionByRouteType, Route> {
    private static final String MISSING_EXPRESSION_MESSAGE = "Route expression is mandatory for custom route";
    private static final String EXPRESSION_SET_FOR_NON_CUSTOM_ROUTE_MESSAGE =
            "Route expression can be set only custom route";

    @Override
    public boolean isValid(Route value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.getType() == Route.Type.CUSTOM && !value.getExpression().isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MISSING_EXPRESSION_MESSAGE)
                    .addConstraintViolation();
            return false;
        }
        if (value.getType() != Route.Type.CUSTOM && value.getExpression().isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(EXPRESSION_SET_FOR_NON_CUSTOM_ROUTE_MESSAGE)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
