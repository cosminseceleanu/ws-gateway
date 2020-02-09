package domain.model

import domain.exceptions.GenericException
import domain.model.RouteType.RouteType

case class Route(routeType: RouteType, name: String, backends: Set[Backend[BackendSettings]],
                 expression: Option[Expression[Boolean]]) {

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

  def appliesTo(json: String): Boolean = {
    if (routeType != RouteType.CUSTOM) {
      throw GenericException(s"expression can be evaluated only for custom routes")
    }
    expression match {
      case Some(e) => e.evaluate(json)
      case None => throw GenericException("Custom route does not have an expression")
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

  def apply(routeType: RouteType, name: String): Route = new Route(routeType, name, Set.empty, Option.empty)
  def apply(routeType: RouteType, name: String, backends: Set[Backend[BackendSettings]]): Route = {
    new Route(routeType, name, backends, Option.empty)
  }
  def apply(routeType: RouteType, name: String,
            backends: Set[Backend[BackendSettings]],
            expression: Option[Expression[Boolean]]): Route = {
    new Route(routeType, name, backends, expression)
  }
}
