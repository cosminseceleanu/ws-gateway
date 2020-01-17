package gateway

import akka.actor.ActorRef
import akka.stream.{FlowShape, Materializer, OverflowStrategy, QueueOfferResult}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition, RunnableGraph, Sink, Source, SourceQueueWithComplete}
import domain.model.{Endpoint, Backend, Route}
import gateway.events.{Connected, Disconnected, Event, OutboundEvent, InboundEvent, ReceivedEvent}
import gateway.backend.{BlackHoleConnector, BackendConnector}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

class EventHandler(private val connectionId: String, private val endpoint: Endpoint, private val out: ActorRef) (implicit materializer: Materializer) {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val backendConnector: BackendConnector = new BlackHoleConnector
  private val sendToUserSink = Sink.foreach[OutboundEvent](msg => out ! msg.payload)
  private val source = Source.queue[Event](endpoint.bufferSize, OverflowStrategy.backpressure)

  private val inboundFlow = Flow[Event].collectType[InboundEvent]
    .map({
      case e: Connected => (e, Set(endpoint.getConnectRoute))
      case e: Disconnected => (e, Set(endpoint.getDisconnectRoute))
      case e: ReceivedEvent => (e, getCustomRoutesOrDefault(endpoint))
    }).map(eventWithRoutesPair => {
      val backends = eventWithRoutesPair._2.flatMap(_ => Set(Backend.blackHole()))
      (eventWithRoutesPair._1, backends)
    }).mapAsync(endpoint.backendParallelism)(eventOutboundsPair => {
      val (event, backends) = eventOutboundsPair
      Future.sequence(backends.map(o => backendConnector.sendEvent(event, o.destination)))
          .map(responses => getOnlyErrors(responses))
          .map(_.map(e => (event, e)))
    }).mapConcat(eventErrorPairs => eventErrorPairs.map(_._1))

  private def getOnlyErrors(responses: Set[Either[Exception, Unit]]) = {
    responses.filter(_.isLeft).map(_.left.get)
  }

  private def getCustomRoutesOrDefault(endpoint: Endpoint): Set[Route] = {
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
      case _: InboundEvent => 0
      case _: OutboundEvent => 1
    }))
    val merge  = builder.add(Merge[Event](2))

    partition.out(0) ~> inboundFlow ~> merge
    partition.out(1) ~> Flow[Event].collectType[OutboundEvent] ~> merge

    FlowShape(partition.in, merge.out)
  }

  private val gatewayStream: RunnableGraph[SourceQueueWithComplete[Event]] = source
    .via(graph)
    .collectType[OutboundEvent]
    .to(sendToUserSink)

  private val queue = gatewayStream.run()

  /**
   * Attention, this method is not thread safe!!!
   * It should be used only from an actor context and one instance shouldn't be shared across multiple actors
   */
  def handle(event: Event): Future[Try[Void]] = {
    queue.offer(event).map({
      case QueueOfferResult.Enqueued =>
        logger.debug(s"message enqueued connectionId=$connectionId")
        Success(None.orNull)
      case QueueOfferResult.Dropped =>
        logger.error(s"message dropped event=$event connectionId=$connectionId")
        Failure(new RuntimeException("Message was dropped"))
      case QueueOfferResult.QueueClosed =>
        logger.error(s"queue was closed event=$event connectionId=$connectionId")
        Failure(new RuntimeException("Queue was closed"))
      case QueueOfferResult.Failure(e) =>
        logger.error(s"message was enqueue due to a failure event=$event connectionId=$connectionId", e)
        Failure(e)
    }).recover({
      case e: Exception =>
        logger.error(s"failed to offer message to queue event=$event connectionId=$connectionId", e)
        Failure(e)
    })
  }
}
