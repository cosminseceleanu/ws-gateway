package gateway.connection

import akka.actor._
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl._
import domain.model.Endpoint

import scala.concurrent.duration._

object WsConnectionActor {
  def props(out: ActorRef, endpoint: Endpoint): Props = Props(new WsConnectionActor(out, endpoint))
}

class WsConnectionActor(out: ActorRef, endpoint: Endpoint) extends Actor {
  private implicit val mat: Materializer = Materializer(context)

  private val sink = Sink.foreach[String](msg => out ! msg)
  private val source = Source.queue[String](2, OverflowStrategy.backpressure)
  private val flow = Flow[String].map(m => s"$m ack")
    .throttle(1, 10.seconds)
    .filter(msg => msg.contains("hey"))

  private val queue = source.via(flow)
    .to(sink)
    .run()

  override def preStart(): Unit = {
    println("start")
  }

  override def postStop(): Unit = {
    println("stop")
  }

  def receive: PartialFunction[Any, Unit] = {
    case msg: String => queue.offer(msg)
//    case msg: String => Source.single(msg).to(sink).run()
  }
}
