package api.exceptionmappers

import common.rest.errors.{ErrorResource, ExceptionMapper}
import domain.exceptions.IncorrectExpressionException
import javax.inject.Singleton
import play.mvc.Http.Status

@Singleton
class IncorrectExpressionExceptionMapper extends ExceptionMapper {
  override type E = IncorrectExpressionException

  override def mapToError(exception: IncorrectExpressionException): (Int, ErrorResource) = {
    (Status.BAD_REQUEST, ErrorResource("IncorrectExpression", exception.getMessage))
  }

  override def getExceptionClass: Class[IncorrectExpressionException] = classOf[IncorrectExpressionException]
}
