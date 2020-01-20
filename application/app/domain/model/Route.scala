package domain.model

import domain.model.RouteType.RouteType

case class Route(routeType: RouteType, name: String) {

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
  def connect(): Route = Route(RouteType.CONNECT, "Connect")
  def disconnect(): Route = Route(RouteType.DISCONNECT, "Disconnect")
  def default(): Route = Route(RouteType.DEFAULT, "Default route")
}
