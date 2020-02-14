package domain.model

import common.validation.Validatable
import domain.exceptions.RouteNotFoundException
import domain.model.AuthenticationMode.AuthenticationMode
import domain.model.RouteType.RouteType
import javax.validation.Valid
import javax.validation.constraints.{NotBlank, NotNull, Pattern}

import scala.annotation.meta.field

case class Endpoint(
                     id: String,
                     @(NotBlank @field) @(Pattern @field)(regexp="^(?!\\/api\\/internal).*") path: String,
                     @(Valid @field) @(NotNull @field) private val configuration: EndpointConfiguration
                   ) extends Validatable {
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

  def findCustomRoute(json: String): Option[Route] = getCustomRoutes.find(r => r.appliesTo(json))

  def getRoute(routeType: RouteType): Option[Route] = configuration.routes.find(_.routeType == routeType)
  def getRoutes(routeType: RouteType): Set[Route] = configuration.getRoutes(routeType)
  def hasRoute(routeType: RouteType): Boolean = configuration.routes.exists(_.routeType == routeType)

  def addRoutes(routes: Set[Route]): Endpoint = {
    val newConfig = configuration.copy(routes = configuration.routes.++(routes))
    copy(configuration = newConfig)
  }

  def routes: Set[Route] = configuration.routes
  def filters: Set[Filter] = configuration.filters
  def authenticationMode: AuthenticationMode = configuration.authenticationMode

  def bufferSize: Int = configuration.bufferSize
  def backendParallelism: Int = configuration.backendParallelism

  def matchesPath(targetPath: String): Boolean = targetPath.matches(path)
}

object Endpoint {
  def apply(id: String, path: String): Endpoint = {
    new Endpoint(id, path, EndpointConfiguration(Set.empty, Set.empty))
  }

  def apply(id: String, path: String, endpointConfiguration: EndpointConfiguration): Endpoint = {
    new Endpoint(id, path, endpointConfiguration)
  }

  def apply(id: String, path: String, filters: Set[Filter], routes: Set[Route]): Endpoint = {
    new Endpoint(id, path, EndpointConfiguration(filters, routes))
  }

  def apply(id: String, path: String, filters: Set[Filter], routes: Set[Route], authenticationMode: AuthenticationMode): Endpoint = {
    new Endpoint(id, path, EndpointConfiguration(filters, routes, authenticationMode))
  }
}

