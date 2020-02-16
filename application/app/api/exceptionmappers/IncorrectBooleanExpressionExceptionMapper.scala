package api.exceptionmappers

import common.rest.errors.{ErrorResource, ExceptionMapper}
import domain.exceptions.IncorrectBooleanExpressionException
import javax.inject.Singleton
import play.mvc.Http.Status

@Singleton
class IncorrectBooleanExpressionExceptionMapper extends ExceptionMapper {
  override type E = IncorrectBooleanExpressionException

  override def mapToError(exception: IncorrectBooleanExpressionException): (Int, ErrorResource) = {
    (Status.BAD_REQUEST, ErrorResource("IncorrectBooleanExpression", exception.getMessage))
  }

  override def getExceptionClass: Class[IncorrectBooleanExpressionException] = classOf[IncorrectBooleanExpressionException]
}
