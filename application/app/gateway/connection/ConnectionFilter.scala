package gateway.connection

import domain.model.Endpoint
import play.api.mvc.RequestHeader

import scala.concurrent.Future

trait ConnectionFilter {
  def filter(endpoint: Endpoint, request: RequestHeader): Future[RequestHeader]
}
