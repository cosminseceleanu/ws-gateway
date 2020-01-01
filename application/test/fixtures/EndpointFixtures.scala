package fixtures

import api.rest.resources.{EndpointResource, FilterResource, RouteResource}
import domain.model.{AuthenticationMode, RouteType}

object EndpointFixtures {
  def fullEndpointResource(): EndpointResource = {
    val filters = FilterResource(Set("1.1.1.1"), Set("2.2.2.2"), Set("host1"), Set("host2"))
    val routes = defaultRoutes

    EndpointResource(None, "/test", filters, routes, AuthenticationMode.NONE.toString)
  }

  def defaultRoutes: Set[RouteResource] = {
    Set(
      RouteResource(RouteType.CONNECT.toString, "Connect"),
      RouteResource(RouteType.DISCONNECT.toString, "Disconnect"),
      RouteResource(RouteType.DEFAULT.toString, "Default")
      )
  }
}
