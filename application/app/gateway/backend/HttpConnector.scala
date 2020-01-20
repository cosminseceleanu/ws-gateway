package gateway.backend

import gateway.events.InboundEvent
import javax.inject.Inject
import play.api.libs.ws._
import play.mvc.Http.Status

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._


class HttpConnector @Inject()(httpClient: WSClient) extends BackendConnector {

  override def sendEvent(outboundEvent: InboundEvent, destination: String): Future[Either[Exception, Unit]] = {
    httpClient.url(destination)
      .addHttpHeaders("Content-Type" -> "application/json")
      .post(outboundEvent.payload)
      .map(response => response.status match {
        case s if s >= Status.BAD_REQUEST => Left(InvalidHttpStatusCodeException(response.status))
        case _ => Right()
      })
      .recover({
        case e: Exception => Left(e)
      })

  }
}
