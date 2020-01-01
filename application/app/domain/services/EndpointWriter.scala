package domain.services

import domain.model.{Endpoint, Route, RouteType}
import domain.repositories.EndpointRepository
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EndpointWriter @Inject()(@Named("endpointRepo") endpointRepository: EndpointRepository, endpointsProvider: EndpointsProvider) {
  private val DEFAULT_ROUTE_TYPES = Map(
    RouteType.CONNECT -> Route.connect(),
    RouteType.DISCONNECT -> Route.disconnect(),
    RouteType.DEFAULT -> Route.default())

  def create(endpoint: Endpoint): Future[Endpoint] = endpointRepository.create(ensureDefaultRoutes(endpoint))

  def update(id: String, endpoint: Endpoint): Future[Endpoint] = {
    endpointsProvider.get(id)
      .map(_ => ensureDefaultRoutes(endpoint))
      .flatMap(e => {endpointRepository.update(endpoint)})
  }

  private def ensureDefaultRoutes(endpoint: Endpoint): Endpoint = {
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
