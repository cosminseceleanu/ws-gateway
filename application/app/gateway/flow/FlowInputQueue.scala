package gateway.flow

import akka.stream.QueueOfferResult
import akka.stream.scaladsl.SourceQueueWithComplete
import gateway.events.Event
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

class FlowInputQueue(private val queue: SourceQueueWithComplete[Event], private val connectionId: String) {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

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
