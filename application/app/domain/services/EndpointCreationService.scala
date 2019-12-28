package domain.services

import domain.model.{Endpoint, Route, RouteType}
import domain.repositories.EndpointRepository
import javax.inject.{Inject, Named}

import scala.concurrent.Future

class EndpointCreationService @Inject()(@Named("endpointRepo") endpointRepository: EndpointRepository) {
  private val DEFAULT_ROUTE_TYPES = Map(
    RouteType.CONNECT -> Route.connect(),
    RouteType.DISCONNECT -> Route.disconnect(),
    RouteType.DEFAULT -> Route.default())

  def create(endpoint: Endpoint): Future[Endpoint] = {
    endpointRepository.save(ensureDefaultRoutes(endpoint))
  }

  def ensureDefaultRoutes(endpoint: Endpoint): Endpoint = {
    val missingRoutes = DEFAULT_ROUTE_TYPES
      .filterKeys(routeType => !endpoint.hasRoute(routeType))
      .values
      .toSet
    if (missingRoutes.isEmpty) {
      endpoint
    } else {
      endpoint.addRoutes(missingRoutes)
    }
  }
}
