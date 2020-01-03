package common

import java.util.concurrent.LinkedBlockingDeque

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.concurrent.Future

class AkkaWebSocketClient {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  type MessageQueue = LinkedBlockingDeque[String]
  type SourceQueue = SourceQueueWithComplete[String]

  private val messageQueue = new LinkedBlockingDeque[String]()

  def connect(serverUrl: String): Future[(MessageQueue, SourceQueue)] = {
    val incoming: Sink[Message, Future[Done]] = Sink.foreach {
      case TextMessage.Strict(s) => messageQueue.offer(s)
      case TextMessage.Streamed(s) => s.runFold("")(_ + _).foreach(messageQueue.offer)
      case BinaryMessage.Strict(s) => messageQueue.offer(s.utf8String)
      case BinaryMessage.Streamed(s) => s.runFold("")(_ + _.utf8String).foreach(messageQueue.offer)
    }
    val sourceQueue = Source.queue[String](Int.MaxValue, OverflowStrategy.backpressure)
      .map { msg => TextMessage.Strict(msg) }
    val (sourceMat, source) = sourceQueue.preMaterialize()
    val flow: Flow[Message, Message, Future[Done]] = Flow.fromSinkAndSourceMat(incoming, source)(Keep.left)

    val (upgradeResponse, closed) = Http().singleWebSocketRequest(WebSocketRequest(serverUrl), flow)
    closed.foreach(_ => println("closed"))
    upgradeResponse.map { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        (messageQueue, sourceMat)
      } else {
        throw new WsConnectionException(upgrade.response.status.intValue(), s"Connection failed: ${upgrade.response.status}")
      }
    }
  }
}
