package fixtures

import api.rest.resources.{EndpointResource, FilterResource, RouteResource}
import domain.model.{AuthenticationMode, RouteType}

object EndpointFixtures {

  def fromPath(path: String): EndpointResource = {
    val default = fullEndpointResource()

    default.copy(path = path)
  }

  def withRoutes(routes: Set[RouteResource]): EndpointResource = {
    val default = fullEndpointResource()

    default.copy(routes = routes)
  }

  def withRoutesAndPath(routes: Set[RouteResource], path: String): EndpointResource = {
    val default = fullEndpointResource()

    default.copy(routes = routes, path = path)
  }

  def fullEndpointResource(): EndpointResource = {
    val filters = FilterResource(Set("1.1.1.1"), Set("2.2.2.2"), Set("host1"), Set("host2"))
    val routes = defaultRoutes

    EndpointResource(None, "/test", None, None, filters, routes, AuthenticationMode.NONE.toString)
  }

  def defaultRoutes: Set[RouteResource] = {
    Set(
      RouteResource(RouteType.CONNECT.toString, "Connect"),
      RouteResource(RouteType.DISCONNECT.toString, "Disconnect"),
      RouteResource(RouteType.DEFAULT.toString, "Default")
      )
  }
}
