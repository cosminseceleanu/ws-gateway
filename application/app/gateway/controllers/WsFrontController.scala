package gateway.controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import gateway.connection.ConnectionManager
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

class WsFrontController @Inject() (
                                    val cc: ControllerComponents,
                                    val connectionManager: ConnectionManager
                                  ) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  def gateway(path: String): WebSocket = WebSocket.acceptOrResult[JsValue, JsValue] { request =>
    connectionManager.connect(path, request)
  }
}
