package gateway.authentication


import domain.model.{Authentication, Endpoint}
import javax.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.RequestHeader
import play.mvc.Http.{HeaderNames, Status}

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class BearerAuthenticator @Inject()(httpClient: WSClient) extends Authenticator {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def isAuthenticated(request: RequestHeader, endpoint: Endpoint): Future[Boolean] = {
    val auth = endpoint.authentication.asInstanceOf[Authentication.Bearer]

    request.headers.get(HeaderNames.AUTHORIZATION) match {
      case Some(value) => checkToken(auth, value, endpoint)
      case None => Future.successful(false)
    }
  }

  private def checkToken(auth: Authentication.Bearer, value: String, endpoint: Endpoint) = {
    httpClient.url(auth.authorizationServerUrl)
      .addHttpHeaders("Content-Type" -> "application/json")
      .withRequestTimeout(Duration(10, SECONDS))
      .post(Json.obj("access_token" -> value.replace("Bearer ", "")))
      .map(response => response.status match {
        case s if s >= Status.BAD_REQUEST => false
        case _ => true
      }).recover({
      case e: Exception => logger.error(s"Authentication check failed for endpoint=${endpoint.path}", e)
        false
    })
  }

  override def supports(endpoint: Endpoint): Boolean = endpoint.authentication.isInstanceOf[Authentication.Bearer]
}
