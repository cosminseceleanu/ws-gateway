package gateway.connection

import java.util.UUID

import akka.actor._
import akka.stream.Materializer
import domain.model.Endpoint
import gateway.EventHandler
import gateway.events.{Connected, Disconnected, ReceivedEvent}
import play.api.libs.json.JsValue

object ConnectionActor {
  def props(out: ActorRef, endpoint: Endpoint): Props = Props(new ConnectionActor(out, endpoint, UUID.randomUUID().toString))
}

class ConnectionActor(out: ActorRef, endpoint: Endpoint, connectionId: String) extends Actor with ActorLogging {
  private implicit val mat: Materializer = Materializer(context)
  private val eventHandler = new EventHandler(connectionId, endpoint, out);

  override def preStart(): Unit = {
    log.info("Gateway WebSocket connection={} established", connectionId)
    eventHandler.handle(Connected(connectionId))
  }

  override def postStop(): Unit = {
    log.info("Gateway WebSocket connection={} terminated", connectionId)
    eventHandler.handle(Disconnected(connectionId))
  }

  def receive: PartialFunction[Any, Unit] = {
    case payload: JsValue => eventHandler.handle(ReceivedEvent(connectionId, payload))
  }
}
