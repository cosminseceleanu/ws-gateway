package gateway.connection

import domain.model.Endpoint
import javax.inject.{Inject, Singleton}
import play.api.mvc.RequestHeader

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class FilterChain @Inject() (private val filters: Set[ConnectionFilter]) {

  def filter(endpoint: Endpoint, request: RequestHeader): Future[RequestHeader] = {
    filters.foldRight(Future.successful(request))((filter, request) => {
      request.flatMap(r => filter.filter(endpoint, r))
    })
  }
}
