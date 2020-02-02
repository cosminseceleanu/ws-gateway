package gateway.flow

import akka.NotUsed
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition, RunnableGraph, Sink, Source, SourceQueueWithComplete}
import akka.stream.{FlowShape, Materializer, OverflowStrategy}
import gateway.connection.ConnectionContext
import gateway.events.{Event, InboundEvent, OutboundEvent}
import javax.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}

@Singleton
class GatewayFlowFactory @Inject()(private val inboundFlowFactory: InboundFlowFactory) {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def create(connection: ConnectionContext)(implicit materializer: Materializer): FlowInputQueue = {

    val sendToUserSink = Sink.foreach[OutboundEvent](msg => connection.out ! msg.payload)
    val source = Source.queue[Event](connection.endpoint.bufferSize, OverflowStrategy.backpressure)
    val inboundFlow = inboundFlowFactory.create(connection.endpoint)
    val graph = createPartitionedGraph(inboundFlow)

    val gatewayStream: RunnableGraph[SourceQueueWithComplete[Event]] = source
      .via(graph)
      .collectType[OutboundEvent]
      .to(sendToUserSink)

    val queue: SourceQueueWithComplete[Event] = gatewayStream.run()

    new FlowInputQueue(queue, connection.connectionId)
  }

  private def createPartitionedGraph(inboundFlow: Flow[Event, InboundEvent, NotUsed]) =  GraphDSL.create() { implicit builder =>
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
}
