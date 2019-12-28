package gateway

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

class WsFrontController @Inject() (cc: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  def socket: WebSocket = WebSocket.accept[String, String] { request =>
    Flow[String].map(in => {
      s"$in ack"
    })
  }
}
