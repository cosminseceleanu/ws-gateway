package domain.model

import domain.exceptions.RouteNotFoundException
import domain.model.AuthenticationMode.AuthenticationMode
import domain.model.RouteType.RouteType

case class Endpoint(id: String, path: String, private val configuration: EndpointConfiguration) {
  def getConnectRoute: Route = getRoute(RouteType.CONNECT) match {
    case Some(r) => r
    case None => throw RouteNotFoundException("Connect route is not defined")
  }

  def getDisconnectRoute: Route = getRoute(RouteType.DISCONNECT) match {
    case Some(r) => r
    case None => throw RouteNotFoundException("Disconnect route is not defined")
  }

  def getDefaultRoute: Route = getRoute(RouteType.DEFAULT) match {
    case Some(r) => r
    case None => throw RouteNotFoundException("Default route is not defined")
  }

  def getCustomRoutes: Set[Route] = getRoutes(RouteType.CUSTOM)

  def getRoute(routeType: RouteType): Option[Route] = configuration.routes.find(_.routeType == routeType)
  def getRoutes(routeType: RouteType): Set[Route] = configuration.routes.filter(_.routeType == routeType)
  def hasRoute(routeType: RouteType): Boolean = configuration.routes.exists(_.routeType == routeType)

  def addRoutes(routes: Set[Route]): Endpoint = {
    val newConfig = configuration.copy(routes = configuration.routes.++(routes))
    copy(configuration = newConfig)
  }

  def routes: Set[Route] = configuration.routes
  def filters: Set[Filter] = configuration.filters
  def authenticationMode: AuthenticationMode = configuration.authenticationMode

  def bufferSize: Int = configuration.bufferSize
  def outboundParallelism: Int = configuration.outboundParallelism

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

