package gateway.authentication

import domain.model.Endpoint
import play.api.mvc.RequestHeader

import scala.concurrent.Future

trait Authenticator {
  def isAuthenticated(request: RequestHeader, endpoint: Endpoint): Future[Boolean]
  def supports(endpoint: Endpoint): Boolean
}
