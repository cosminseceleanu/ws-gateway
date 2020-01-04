package gateway.outbound

import gateway.events.OutboundEvent

import scala.concurrent.Future
import scala.util.Try

trait OutboundConnector {
  def sendEvent(outboundEvent: OutboundEvent, destination: String): Future[Try[Any]]
}
