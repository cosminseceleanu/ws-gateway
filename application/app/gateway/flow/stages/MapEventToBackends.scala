package gateway.flow.stages

import domain.model.{Backend, BackendSettings, Endpoint, Route}
import gateway.backend.ConnectorResolver
import gateway.events.{Connected, Disconnected, InboundEvent, ReceivedEvent}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json

@Singleton
class MapEventToBackends @Inject()(private val connectorResolver: ConnectorResolver) {

  def run(event: InboundEvent, endpoint: Endpoint): Set[Backend[BackendSettings]] = {
    val route = event match {
      case _: Connected => endpoint.getConnectRoute
      case _: Disconnected => endpoint.getDisconnectRoute
      case _: ReceivedEvent => getCustomRoutesOrDefault(event, endpoint)
    }

    route.backends
  }

  private def getCustomRoutesOrDefault(event: InboundEvent, endpoint: Endpoint): Route = {
    val json = Json.stringify(event.payload)
    endpoint.findCustomRoute(json)
      .getOrElse(endpoint.getDefaultRoute)
  }
}
