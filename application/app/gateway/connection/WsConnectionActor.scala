package gateway.connection

import java.util.UUID

import akka.actor._
import akka.stream.{FlowShape, Materializer, OverflowStrategy}
import akka.stream.scaladsl._
import domain.model.{Endpoint, Outbound, Route}
import gateway.events.{Connected, Disconnected, Event, InboundEvent, OutboundEvent, ReceivedEvent}
import gateway.outbound.{BlackHoleConnector, OutboundConnector}
import play.api.libs.json.JsValue

import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object WsConnectionActor {
  def props(out: ActorRef, endpoint: Endpoint): Props = Props(new WsConnectionActor(out, endpoint, UUID.randomUUID().toString))
}

class WsConnectionActor(out: ActorRef, endpoint: Endpoint, connectionId: String) extends Actor with ActorLogging {
  private val outboundConnector: OutboundConnector = new BlackHoleConnector

  private implicit val mat: Materializer = Materializer(context)
  private val sendToUserSink = Sink.foreach[InboundEvent](msg => out ! msg)
  private val source = Source.queue[Event](50, OverflowStrategy.backpressure)

  private val outboundFlow = Flow[Event].collectType[OutboundEvent]
    .map({
      case e: Connected => (e, Set(endpoint.getConnectRoute))
      case e: Disconnected => (e, Set(endpoint.getDisconnectRoute))
      case e: ReceivedEvent => (e, getCustomsRoutesOrDefault(endpoint))
    }).map(eventWithRoutesPair => {
    val outbounds = eventWithRoutesPair._2.flatMap(_ => Set(Outbound.blackHole()))
    (eventWithRoutesPair._1, outbounds)
  }).mapAsync(2)(eventWithOutboundsPair => {
    val (event, outbounds) = eventWithOutboundsPair
    Future.sequence(outbounds.map(o => outboundConnector.sendEvent(event, o.destination)))
    Future.successful(event)
  })

  def getCustomsRoutesOrDefault(endpoint: Endpoint): Set[Route] = {
    val custom = endpoint.getCustomRoutes
    if (custom.isEmpty) {
      Set(endpoint.getDefaultRoute)
    } else {
      custom
    }
  }

  private val graph = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val partition = builder.add(Partition[Event](2, {
      case _: OutboundEvent => 0
      case _: InboundEvent => 1
    }))
    val merge  = builder.add(Merge[Event](2))

    partition.out(0) ~> outboundFlow ~> merge
    partition.out(1) ~> Flow[Event].collectType[InboundEvent] ~> merge

    FlowShape(partition.in, merge.out)
  }

  private val queue = source.via(graph).collectType[InboundEvent].to(sendToUserSink).run()

  override def preStart(): Unit = {
    log.info("Gateway ws connection={} established", connectionId)
    queue.offer(Connected(connectionId))
  }

  override def postStop(): Unit = {
    log.info("Gateway ws connection={} terminated", connectionId)
    queue.offer(Disconnected(connectionId))
  }

  def receive: PartialFunction[Any, Unit] = {
    case payload: JsValue => queue.offer(ReceivedEvent(connectionId, payload))
  }
}
