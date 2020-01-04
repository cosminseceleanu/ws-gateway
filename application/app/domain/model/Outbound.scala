package domain.model

import domain.model.OutboundChannel.OutboundChannel

case class Outbound(outboundChannel: OutboundChannel, destination: String)

object Outbound {
  def blackHole(): Outbound = Outbound(OutboundChannel.BLACK_HOLE, "black hole")
}
