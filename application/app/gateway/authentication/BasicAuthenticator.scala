package gateway.authentication

import java.nio.charset.StandardCharsets
import java.util.Base64

import domain.model.{Authentication, Endpoint}
import javax.inject.Singleton
import play.api.mvc.RequestHeader
import play.mvc.Http.HeaderNames

import scala.concurrent.Future

@Singleton
class BasicAuthenticator extends Authenticator {
  override def isAuthenticated(request: RequestHeader, endpoint: Endpoint): Future[Boolean] = {
    val auth = endpoint.authentication.asInstanceOf[Authentication.Basic]
    val token = encodeBase64(s"${auth.username}:${auth.password}")
    val expected = s"Basic ${token}"

    request.headers.get(HeaderNames.AUTHORIZATION) match {
      case Some(value) => Future.successful(expected.equals(value))
      case None => Future.successful(false)
    }
  }

  private def encodeBase64(value: String) = {
    Base64.getEncoder.encodeToString(value.getBytes(StandardCharsets.UTF_8))
  }

  override def supports(endpoint: Endpoint): Boolean = endpoint.authentication.isInstanceOf[Authentication.Basic]
}
