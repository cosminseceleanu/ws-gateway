package gateway.backend

import domain.model.BackendType.BackendType
import domain.model.{BackendType, HttpSettings}
import gateway.events.InboundEvent
import javax.inject.Inject
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.ws._
import play.mvc.Http.Status

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._


class HttpConnector @Inject()(httpClient: WSClient) extends BackendConnector {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override type T = HttpSettings

  override def supports(backendType: BackendType): Boolean = backendType == BackendType.HTTP

  override def sendEvent(event: InboundEvent, destination: String, settings: HttpSettings): Future[Either[Exception, Unit]] = {
    httpClient.url(destination)
      .addHttpHeaders("Content-Type" -> "application/json")
      .addHttpHeaders(settings.additionalHeaders.toList:_*)
      .withRequestTimeout(Duration(settings.timeoutInMillis, MILLISECONDS))
      .post(event.payload)
      .map(response => response.status match {
        case s if s >= Status.BAD_REQUEST => Left(InvalidHttpStatusCodeException(response.status))
        case _ => Right()
      })
      .recover({
        case e: Exception =>
          logger.error(s"Request to $destination failed with ${e.getMessage}", e)
          Left(e)
      })

  }
}
