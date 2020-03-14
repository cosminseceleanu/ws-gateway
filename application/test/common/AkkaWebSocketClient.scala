package common

import java.util.concurrent.LinkedBlockingDeque

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, SourceQueueWithComplete}
import akka.stream.{Materializer, OverflowStrategy}
import play.api.Logger

import scala.collection.immutable
import scala.concurrent.Future

class AkkaWebSocketClient {
  private val logger = Logger(this.getClass)
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: Materializer = Materializer(system)

  import system.dispatcher

  type MessageQueue = LinkedBlockingDeque[String]
  type SourceQueue = SourceQueueWithComplete[String]

  private val messageQueue = new LinkedBlockingDeque[String]()

  def connect(serverUrl: String): Future[(MessageQueue, SourceQueue)] = connectWithHeaders(serverUrl, immutable.Seq.empty[HttpHeader])

  def connectWithHeaders(serverUrl: String, headers: immutable.Seq[HttpHeader]): Future[(MessageQueue, SourceQueue)] = {
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

    val request = new WebSocketRequest(uri = serverUrl, extraHeaders = headers)
    val (upgradeResponse, closed) = Http().singleWebSocketRequest(request, flow)
    closed.foreach(_ => logger.info("closed websocket connection"))
    upgradeResponse.map { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        (messageQueue, sourceMat)
      } else {
        throw WsConnectionException(upgrade.response.status.intValue(), s"Connection failed: ${upgrade.response.status}")
      }
    }
  }
}
