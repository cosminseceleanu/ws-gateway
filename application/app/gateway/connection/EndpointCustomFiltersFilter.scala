package gateway.connection

import domain.exceptions.AccessDeniedException
import domain.model.Endpoint
import javax.inject.Singleton
import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.RequestHeader

import scala.concurrent.Future

@Singleton
class EndpointCustomFiltersFilter extends ConnectionFilter {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def filter(endpoint: Endpoint, request: RequestHeader): Future[RequestHeader] = {
    endpoint.filters.find(f => !f.isAllowed(request.headers.toSimpleMap)) match {
      case Some(f) =>
        logger.info(s"filter=${f.name} does not allow request to endpoint=${endpoint.path}")
        Future.failed(AccessDeniedException())
      case None => Future.successful(request)
    }
  }
}
