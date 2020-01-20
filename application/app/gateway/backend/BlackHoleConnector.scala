package gateway.backend

import gateway.events.InboundEvent
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

class BlackHoleConnector extends BackendConnector {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def sendEvent(inboundEvent: InboundEvent, destination: String): Future[Either[Exception, Unit]] = {
    logger.info(s"send outbound event of type ${inboundEvent.getClass.getName} for connection=${inboundEvent.connectionId} to destination=$destination")
    Future.successful(Right())
  }
}
