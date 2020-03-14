package gateway.authentication

import domain.exceptions.AccessDeniedException
import domain.model.Endpoint
import gateway.connection.ConnectionFilter
import javax.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.RequestHeader

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthenticationConnectionFilter @Inject() (private val checkers: Set[Authenticator]) extends ConnectionFilter {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def filter(endpoint: Endpoint, request: RequestHeader): Future[RequestHeader] = {
    logger.info(s"Check auth for endpoint=${endpoint.path}")
    checkers.find(_.supports(endpoint)) match {
      case Some(c) => checkAuth(endpoint, request, c)
      case None =>
        logger.error(s"No auth check was found for endpoint=${endpoint.path}")
        Future.failed(AccessDeniedException())
    }
  }

  private def checkAuth(endpoint: Endpoint, request: RequestHeader, c: Authenticator) = {
    c.isAuthenticated(request, endpoint)
      .map(isAuthenticated => {
        if (!isAuthenticated) {
          throw AccessDeniedException()
        }
        request
      })
  }
}
