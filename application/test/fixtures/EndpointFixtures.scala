package fixtures

import api.rest.resources.{AuthenticationResource, EndpointResource, FilterResource, RouteResource}
import domain.model.RouteType

object EndpointFixtures {

  def fromPath(path: String): EndpointResource = {
    val default = fullEndpointResource()

    default.copy(path = path, filters = FilterResource())
  }

  def withRoutes(routes: Set[RouteResource]): EndpointResource = {
    val default = fullEndpointResource()

    default.copy(routes = routes, filters = FilterResource())
  }

  def withRoutesAndPath(routes: Set[RouteResource], path: String): EndpointResource = {
    val default = fullEndpointResource()

    default.copy(routes = routes, path = path, filters = FilterResource())
  }

  def fullEndpointResource(): EndpointResource = {
    val filters = FilterResource(Set("1.1.1.1"), Set("2.2.2.2"), Set("host1"), Set("host2"))
    val routes = defaultRoutes

    EndpointResource(None, "/test", None, None, filters, routes, AuthenticationResource.none())
  }

  def defaultRoutes: Set[RouteResource] = {
    Set(
      RouteResource(RouteType.CONNECT.toString, "Connect"),
      RouteResource(RouteType.DISCONNECT.toString, "Disconnect"),
      RouteResource(RouteType.DEFAULT.toString, "Default")
      )
  }
}
