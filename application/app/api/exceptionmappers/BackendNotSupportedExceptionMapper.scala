package api.exceptionmappers

import common.rest.errors.{ErrorResource, ExceptionMapper}
import domain.exceptions.BackendNotSupportedException
import javax.inject.Singleton
import play.mvc.Http.Status

@Singleton
class BackendNotSupportedExceptionMapper extends ExceptionMapper {
  override type E = BackendNotSupportedException

  override def mapToError(exception: BackendNotSupportedException): (Int, ErrorResource) = {
    (Status.BAD_REQUEST, ErrorResource("BackendNotSupported", exception.getMessage))
  }

  override def getExceptionClass: Class[BackendNotSupportedException] = classOf[BackendNotSupportedException]
}
