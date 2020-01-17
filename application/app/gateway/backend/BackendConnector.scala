package gateway.backend

import gateway.events.InboundEvent

import scala.concurrent.Future

trait BackendConnector {
  def sendEvent(inboundEvent: InboundEvent, destination: String): Future[Either[Exception, Unit]]
}
