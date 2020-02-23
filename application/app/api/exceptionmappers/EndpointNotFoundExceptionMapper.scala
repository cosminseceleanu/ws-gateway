package api.exceptionmappers

import common.rest.errors.{ErrorResource, ExceptionMapper}
import domain.exceptions.EndpointNotFoundException
import javax.inject.Singleton
import play.mvc.Http.Status

@Singleton
class EndpointNotFoundExceptionMapper extends ExceptionMapper {
  override type E = EndpointNotFoundException

  override def mapToError(exception: EndpointNotFoundException): (Int, ErrorResource) = {
    (Status.NOT_FOUND, ErrorResource("EndpointNotFound", exception.getMessage))
  }

  override def getExceptionClass: Class[EndpointNotFoundException] = classOf[EndpointNotFoundException]
}
