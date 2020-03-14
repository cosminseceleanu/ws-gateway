package gateway.authentication

import domain.model.{Authentication, Endpoint}
import javax.inject.Singleton
import play.api.mvc.RequestHeader

import scala.concurrent.Future

@Singleton
class NoAuthChecker extends Authenticator {
  override def isAuthenticated(request: RequestHeader, endpoint: Endpoint): Future[Boolean] = Future.successful(true)

  override def supports(endpoint: Endpoint): Boolean = endpoint.authentication.isInstanceOf[Authentication.None]
}
