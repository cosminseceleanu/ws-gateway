package domain.model

object RouteType extends Enumeration {
  type RouteType = Value
  val CONNECT, DISCONNECT, DEFAULT, CUSTOM = Value
}
