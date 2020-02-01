package gateway.flow

import akka.NotUsed
import akka.stream.scaladsl.Flow
import domain.model.Endpoint
import gateway.backend.{BackendConnector, LoggerConnector}
import gateway.events._
import gateway.flow.dto.BackendResult
import gateway.flow.stages.{MapEventToBackends, SendEventToBackends}
import javax.inject.Inject

@Inject
class InboundFlowFactory @Inject() (
                                     private val mapEventToBackends: MapEventToBackends,
                                     private val sendEventToBackends: SendEventToBackends,
                                   ) {

  private val backendConnector: BackendConnector = new LoggerConnector

  def create(endpoint: Endpoint): Flow[Event, InboundEvent, NotUsed] = Flow[Event].collectType[InboundEvent]
    .map(e => (e, mapEventToBackends.run(e, endpoint)))
    .mapAsync(endpoint.backendParallelism)(e => sendEventToBackends.run(e._1, e._2))
    .mapConcat(responses => handleErrors(responses))

  private def handleErrors(responses: Set[BackendResult]) = responses
    .filter(_.result.isLeft)
    .map(_.event)
}
