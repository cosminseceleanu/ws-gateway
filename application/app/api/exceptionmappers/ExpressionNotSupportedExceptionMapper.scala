package api.exceptionmappers

import common.rest.errors.{ErrorResource, ExceptionMapper}
import domain.exceptions.ExpressionNotSupportedException
import javax.inject.Singleton
import play.mvc.Http.Status

@Singleton
class ExpressionNotSupportedExceptionMapper extends ExceptionMapper {
  override type E = ExpressionNotSupportedException

  override def mapToError(exception: ExpressionNotSupportedException): (Int, ErrorResource) = {
    (Status.BAD_REQUEST, ErrorResource("ExpressionNotSupported", exception.getMessage))
  }

  override def getExceptionClass: Class[ExpressionNotSupportedException] = classOf[ExpressionNotSupportedException]
}
