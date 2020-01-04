package domain.model

object OutboundChannel extends Enumeration {
  type OutboundChannel = Value
  val HTTP, KAFKA, BLACK_HOLE = Value
}
