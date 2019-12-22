package domain.model

object OutboundChannel extends Enumeration {
  type OutboundChannel = Value
  val HTTP, KAFKA = Value
}
