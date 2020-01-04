package gateway.connection

import akka.actor._
import akka.stream.scaladsl._
import akka.stream.{Materializer, OverflowStrategy}
import domain.model.Endpoint

import scala.concurrent.duration._

object PublisherActor {
  case object Ack

  case object StreamInitialized
  case object StreamCompleted
  final case class StreamFailure(ex: Throwable)

  def props(out: ActorRef, endpoint: Endpoint): Props = Props(new PublisherActor(out, endpoint))
}

class PublisherActor(out: ActorRef, endpoint: Endpoint) extends Actor with ActorLogging {
  import PublisherActor._

  override def preStart(): Unit = {
    println("start")
  }

  override def postStop(): Unit = {
    println("stop")
  }

  def receive: Receive = {
    case StreamInitialized =>
      log.info("Stream initialized!")
      out ! "Stream initialized!"
      sender() ! Ack // ack to allow the stream to proceed sending more elements

    case el: String =>
      log.info("Received element: {}", el)
      out ! el
      sender() ! Ack // ack to allow the stream to proceed sending more elements

    case StreamCompleted =>
      log.info("Stream completed!")
      out ! "Stream completed!"
    case StreamFailure(ex) =>
      log.error(ex, "Stream failed!")
  }
}
