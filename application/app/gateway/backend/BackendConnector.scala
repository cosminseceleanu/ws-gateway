package gateway.backend

import domain.model.BackendSettings
import domain.model.BackendType.BackendType
import gateway.events.InboundEvent

import scala.concurrent.Future

trait BackendConnector {
  type T <: BackendSettings

  def supports(backendType: BackendType): Boolean

  def sendEvent(event: InboundEvent, destination: String, settings: T): Future[Either[Exception, Unit]]
}
