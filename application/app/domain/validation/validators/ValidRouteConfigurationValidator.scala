package domain.validation.validators

import domain.model.{EndpointConfiguration, RouteType}
import domain.validation.constraints.ValidRouteConfiguration
import javax.validation.{ConstraintValidator, ConstraintValidatorContext}

class ValidRouteConfigurationValidator extends ConstraintValidator[ValidRouteConfiguration, EndpointConfiguration] {
  override def isValid(configuration: EndpointConfiguration, context: ConstraintValidatorContext): Boolean = {
    if (configuration == null) {
      return true
    }
    if (configuration.routes == null) {
      return true
    }

    checkRouteNumberByTpes(context, configuration, RouteType.CONNECT) &&
      checkRouteNumberByTpes(context, configuration, RouteType.DISCONNECT) &&
      checkRouteNumberByTpes(context, configuration, RouteType.DEFAULT)
  }

  private def checkRouteNumberByTpes(context: ConstraintValidatorContext, configuration: EndpointConfiguration, routeType: RouteType.Value): Boolean = {
    val routes = configuration.getRoutes(routeType)
    if (routes.size > 1) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(s"Endpoint can have at most 1 route of type $routeType")
        .addConstraintViolation()
      return false
    }
    true
  }
}


