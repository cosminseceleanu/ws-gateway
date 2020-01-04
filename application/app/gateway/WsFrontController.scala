package gateway

import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import domain.services.EndpointsProvider
import gateway.connection.ConnectionActor
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.concurrent._
import ExecutionContext.Implicits.global

class WsFrontController @Inject() (
                                    cc: ControllerComponents,
                                    endpointsProvider: EndpointsProvider
                                  ) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  def gateway(path: String): WebSocket = WebSocket.acceptOrResult[JsValue, JsValue] { request =>
    endpointsProvider.getFirstMatch(s"/$path")
      .map({
        case None => Left(NotFound)
        case Some(e) => Right(ActorFlow.actorRef(out => ConnectionActor.props(out, e), e.bufferSize, OverflowStrategy.fail))
      })
  }
}
