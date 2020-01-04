package gateway.outbound

import gateway.events.OutboundEvent

import scala.concurrent.Future

trait OutboundConnector {
  def sendEvent(outboundEvent: OutboundEvent, destination: String): Future[Either[Exception, Unit]]
}
