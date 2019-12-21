package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

class WsFrontController @Inject() (cc: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  def socket = WebSocket.accept[String, String] { request =>
    Flow[String].map(in => {
      println(in)
      s"$in ack"
    })

  }
}
