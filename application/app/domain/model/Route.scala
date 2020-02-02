package domain.model

import domain.model.RouteType.RouteType

case class Route(routeType: RouteType, name: String, backends: Set[Backend[BackendSettings]]) {

  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result = prime * result + routeType.hashCode()
    result = prime * result + (if (name == null) 0 else name.hashCode)
    result
  }

  override def equals(that: Any): Boolean = {
    that match {
      case that: Route => {
        that.canEqual(this) &&
        this.routeType == that.routeType &&
        this.name == that.name
      }
      case _ => false
    }
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Route]
}

object Route {
  private val debugBackend: Set[Backend[BackendSettings]] = Set(Backend.debug())

  def connect(): Route = Route(RouteType.CONNECT, "Connect", debugBackend)
  def connect(backends: Set[Backend[BackendSettings]]): Route = Route(RouteType.CONNECT, "Connect", backends)

  def disconnect(): Route = Route(RouteType.DISCONNECT, "Disconnect", debugBackend)
  def disconnect(backends: Set[Backend[BackendSettings]]): Route = Route(RouteType.DISCONNECT, "Disconnect", backends)


  def default(): Route = Route(RouteType.DEFAULT, "Default route", debugBackend)
  def default(backends: Set[Backend[BackendSettings]]): Route = Route(RouteType.DEFAULT, "Default route", backends)

  def apply(routeType: RouteType, name: String): Route = new Route(routeType, name, Set.empty)
  def apply(routeType: RouteType, name: String, backends: Set[Backend[BackendSettings]]): Route = {
    new Route(routeType, name, backends)
  }
}
