package domain.model

import domain.model.OutboundChannel.OutboundChannel

case class Outbound(outboundChannel: OutboundChannel, destination: String)
