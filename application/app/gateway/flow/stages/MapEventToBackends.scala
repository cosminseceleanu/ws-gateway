package gateway.flow.stages

import domain.model.{Backend, BackendSettings, Endpoint, Route}
import gateway.backend.ConnectorResolver
import gateway.events.{Connected, Disconnected, InboundEvent, ReceivedEvent}
import javax.inject.{Inject, Singleton}

@Singleton
class MapEventToBackends @Inject()(private val connectorResolver: ConnectorResolver) {

  def run(event: InboundEvent, endpoint: Endpoint): Set[Backend[BackendSettings]] = {
    val routes = event match {
      case e: Connected => Set(endpoint.getConnectRoute)
      case e: Disconnected => Set(endpoint.getDisconnectRoute)
      case e: ReceivedEvent => getCustomRoutesOrDefault(endpoint)
    }

    routes.flatMap(_.backends)
  }

  private def getCustomRoutesOrDefault(endpoint: Endpoint): Set[Route] = {
    val custom = endpoint.getCustomRoutes
    if (custom.isEmpty) {
      Set(endpoint.getDefaultRoute)
    } else {
      custom
    }
  }
}
