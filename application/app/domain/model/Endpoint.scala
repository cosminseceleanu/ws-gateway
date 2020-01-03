package domain.model

import domain.model.AuthenticationMode.AuthenticationMode
import domain.model.RouteType.RouteType

case class Endpoint(id: String, path: String, private val configuration: EndpointConfiguration) {
  def hasRoute(routeType: RouteType): Boolean = configuration.routes.exists(_.routeType == routeType)

  def addRoutes(routes: Set[Route]): Endpoint = {
    val newConfig = configuration.copy(routes = configuration.routes.++(routes))
    copy(configuration = newConfig)
  }

  def routes: Set[Route] = configuration.routes
  def filters: Set[Filter] = configuration.filters
  def authenticationMode: AuthenticationMode = configuration.authenticationMode

  def matchesPath(targetPath: String): Boolean = targetPath.matches(path)
}

object Endpoint {
  def apply(id: String, path: String, filters: Set[Filter], routes: Set[Route]): Endpoint = {
    new Endpoint(id, path, EndpointConfiguration(filters, routes))
  }

  def apply(id: String, path: String, filters: Set[Filter], routes: Set[Route], authenticationMode: AuthenticationMode): Endpoint = {
    new Endpoint(id, path, EndpointConfiguration(filters, routes, authenticationMode))
  }
}

