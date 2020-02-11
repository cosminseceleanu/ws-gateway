package domain.validation.validators

import domain.model.{Route, RouteType}
import domain.validation.constraints.ExpressionByRouteType
import javax.validation.{ConstraintValidator, ConstraintValidatorContext}

class ExpressionByRouteTypeValidator extends ConstraintValidator[ExpressionByRouteType, Route] {
  override def isValid(value: Route, context: ConstraintValidatorContext): Boolean = {
    if (value == null) {
      return true
    }
    if (value.routeType == RouteType.CUSTOM && value.expression.isEmpty) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(ExpressionByRouteTypeValidator.MISSING_EXPRESSION_MESSAGE)
        .addConstraintViolation()
      return false
    }
    if (value.routeType != RouteType.CUSTOM && value.expression.isDefined) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(ExpressionByRouteTypeValidator.EXPRESSION_SET_FOR_NON_CUSTOM_ROUTE_MESSAGE)
        .addConstraintViolation()
      return false
    }

    true
  }
}

object ExpressionByRouteTypeValidator {
  val MISSING_EXPRESSION_MESSAGE = "Route expression is mandatory for custom route"
  val EXPRESSION_SET_FOR_NON_CUSTOM_ROUTE_MESSAGE = "Route expression can be set only custom route"
}
