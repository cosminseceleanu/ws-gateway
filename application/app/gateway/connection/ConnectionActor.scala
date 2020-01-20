package gateway.connection

import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import akka.stream.Materializer
import domain.model.Endpoint
import gateway.EventHandler
import gateway.events.{Connected, Disconnected, GenericEvent, ReceivedEvent}
import play.api.libs.json.JsValue

object ConnectionActor {
  def props(out: ActorRef, endpoint: Endpoint, connectionId: String): Props = Props(
    new ConnectionActor(out, endpoint, connectionId))
}

class ConnectionActor(out: ActorRef, endpoint: Endpoint, connectionId: String) extends Actor with ActorLogging {
  private implicit val mat: Materializer = Materializer(context)
  private val eventHandler = new EventHandler(connectionId, endpoint, out)

  private val mediator: ActorRef = DistributedPubSub(context.system).mediator
  private val topic = s"inbound.$connectionId"

  override def preStart(): Unit = {
    log.info("Gateway WebSocket connection={} established", connectionId)
    eventHandler.handle(Connected(connectionId))
    mediator ! Subscribe(topic, self)
  }

  override def postStop(): Unit = {
    log.info("Gateway WebSocket connection={} terminated", connectionId)
    eventHandler.handle(Disconnected(connectionId))
  }

  def receive: PartialFunction[Any, Unit] = {
    case payload: JsValue => eventHandler.handle(ReceivedEvent(connectionId, payload))
    case inboundEvent: GenericEvent => eventHandler.handle(inboundEvent)
    case SubscribeAck(_) =>
      log.info("subscribing to topic={}", topic)
  }
}
