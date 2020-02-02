package gateway.flow.stages

import domain.model.{Backend, BackendSettings}
import gateway.backend.ConnectorResolver
import gateway.events.InboundEvent
import gateway.flow.dto.BackendResult
import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

@Singleton
class SendEventToBackends @Inject()(connectorResolver: ConnectorResolver) {

  def run(event: InboundEvent, backends: Set[Backend[BackendSettings]]): Future[Set[BackendResult]] = {
    Future.sequence(backends.map(backend => sendEventToBackend(event, backend)))
  }

  private def sendEventToBackend(event: InboundEvent, backend: Backend[BackendSettings]) = {
    val connector = connectorResolver.getConnector(backend)

    connector.sendEvent(event, backend.destination, backend.settings.asInstanceOf[connector.T])
      .map(response => BackendResult(event, backend, response))
  }
}
