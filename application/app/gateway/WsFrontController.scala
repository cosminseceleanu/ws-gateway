package gateway

import akka.actor.ActorSystem
import akka.stream.Materializer
import domain.services.EndpointsProvider
import gateway.connection.WsConnectionActor
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.concurrent._
import ExecutionContext.Implicits.global

class WsFrontController @Inject() (
                                    cc: ControllerComponents,
                                    endpointsProvider: EndpointsProvider
                                  ) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  def gateway(path: String): WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    endpointsProvider.getFirstMatch(s"/$path")
      .map({
        case None => Left(NotFound)
        case Some(e) => Right(ActorFlow.actorRef(out => WsConnectionActor.props(out, e)))
      })
  }
}
