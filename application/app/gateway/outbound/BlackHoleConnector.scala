package gateway.outbound
import gateway.events.OutboundEvent
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future
import scala.util.{Success, Try}

class BlackHoleConnector extends OutboundConnector {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def sendEvent(outboundEvent: OutboundEvent, destination: String): Future[Try[Any]] = {
    logger.info(s"send outbound event of type ${outboundEvent.getClass.getName} for connection=${outboundEvent.connectionId} to destination=$destination")
    Future.successful(Success(None))
  }
}
