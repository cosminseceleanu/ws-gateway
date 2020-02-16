package common.rest.errors.mappers

import common.rest.errors.{ErrorResource, ExceptionMapper}
import common.validation.exceptions.ConstraintViolationException
import javax.inject.Singleton
import play.mvc.Http.Status

@Singleton
class ConstraintViolationExceptionMapper extends ExceptionMapper {
  override type E = ConstraintViolationException

  override def mapToError(exception: ConstraintViolationException): (Int, ErrorResource) = {
    val errors = exception.errors
        .toList
        .map(e => s"Validation failed! Cause: ${e.message} property: ${e.propertyPath}")

    (Status.BAD_REQUEST, ErrorResource("ConstraintViolation", exception.getMessage, errors))
  }

  override def getExceptionClass: Class[ConstraintViolationException] = classOf[ConstraintViolationException]
}
