package gateway.backend

import domain.model.BackendType.BackendType
import domain.model.{BackendType, EmptySettings}
import gateway.events.InboundEvent
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

class LoggerConnector extends BackendConnector {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override type T = EmptySettings

  override def supports(backendType: BackendType): Boolean = backendType == BackendType.DEBUG

  override def sendEvent(event: InboundEvent, destination: String, settings: EmptySettings): Future[Either[Exception, Unit]] = {
    logger.info(s"send outbound event of type ${event.getClass.getName} for connection=${event.connectionId} to destination=$destination")
    Future.successful(Right())
  }
}
