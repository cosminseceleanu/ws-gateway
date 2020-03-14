package gateway.connection

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.stream.{Materializer, OverflowStrategy}
import domain.exceptions.{AccessDeniedException, EndpointNotFoundException}
import domain.model.Endpoint
import domain.services.EndpointsProvider
import gateway.flow.GatewayFlowFactory
import javax.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.{RequestHeader, Result, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ConnectionManager @Inject() (
                                    private val endpointsProvider: EndpointsProvider,
                                    private val idGenerator: IdGenerator,
                                    private val gatewayFlowFactory: GatewayFlowFactory,
                                    private val filterChain: FilterChain
                                  ) {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def connect(path: String, request: RequestHeader) (implicit system: ActorSystem, mat: Materializer): Future[Either[Result, Flow[JsValue, JsValue, _]]] = {
    endpointsProvider.getFirstMatch(s"/$path")
      .flatMap(e => filterChain.filter(e, request).map(r => (e, r)))
      .map(endpointRequestPair => doConnect(endpointRequestPair._1, endpointRequestPair._2))
      .recover({
        case _: EndpointNotFoundException => Left(Results.NotFound)
        case _: AccessDeniedException => Left(Results.Unauthorized)
        case e: Exception =>
          logger.error("Failed while creating WS connection", e)
          Left(Results.InternalServerError)
    })
  }

  private def doConnect(e: Endpoint, request: RequestHeader) (implicit system: ActorSystem, mat: Materializer) = {
    Right(ActorFlow.actorRef(out => {
      val id = idGenerator.generate(request.getQueryString("connectionId"))
      ConnectionActor.props(ConnectionContext(id, out, e), gatewayFlowFactory)
    }, e.bufferSize, OverflowStrategy.fail))
  }
}
