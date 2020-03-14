package api.exceptionmappers

import common.rest.errors.{ErrorResource, ExceptionMapper}
import domain.exceptions.AuthenticationNotSupportedException
import javax.inject.Singleton
import play.mvc.Http.Status

@Singleton
class AuthenticationNotSupportedExceptionMapper extends ExceptionMapper {
  override type E = AuthenticationNotSupportedException

  override def mapToError(exception: AuthenticationNotSupportedException): (Int, ErrorResource) = {
    (Status.BAD_REQUEST, ErrorResource("AuthenticationModeNotSupported", exception.getMessage))
  }

  override def getExceptionClass: Class[AuthenticationNotSupportedException] = classOf[AuthenticationNotSupportedException]
}
