package gateway.connection

import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import akka.stream.Materializer
import gateway.events.{Connected, Disconnected, GenericEvent, ReceivedEvent}
import gateway.flow.GatewayFlowFactory
import play.api.libs.json.JsValue

object ConnectionActor {
  def props(connectionContext: ConnectionContext, gatewayFlowFactory: GatewayFlowFactory): Props = Props(
    new ConnectionActor(connectionContext, gatewayFlowFactory))
}

class ConnectionActor(connectionContext: ConnectionContext, gatewayFlowFactory: GatewayFlowFactory) extends Actor with ActorLogging {
  private implicit val mat: Materializer = Materializer(context)
  private val gatewayFlow = gatewayFlowFactory.create(connectionContext)
  private val connectionId = connectionContext.connectionId

  private val mediator: ActorRef = DistributedPubSub(context.system).mediator
  private val topic = s"inbound.$connectionId"

  override def preStart(): Unit = {
    log.info("Gateway WebSocket connection={} established", connectionId)
    gatewayFlow.handle(Connected(connectionId))
    mediator ! Subscribe(topic, self)
  }

  override def postStop(): Unit = {
    log.info("Gateway WebSocket connection={} terminated", connectionId)
    gatewayFlow.handle(Disconnected(connectionId))
  }

  def receive: PartialFunction[Any, Unit] = {
    case payload: JsValue => gatewayFlow.handle(ReceivedEvent(connectionId, payload))
    case inboundEvent: GenericEvent => gatewayFlow.handle(inboundEvent)
    case SubscribeAck(_) =>
      log.info("subscribing to topic={}", topic)
  }
}
