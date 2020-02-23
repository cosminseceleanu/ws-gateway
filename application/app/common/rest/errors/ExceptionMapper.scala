package common.rest.errors

trait ExceptionMapper {
  type E <: Throwable

  def supports(exception: Throwable): Boolean = getExceptionClass.isInstance(exception)

  def mapToError(exception: E): (Int, ErrorResource)

  def getExceptionClass: Class[E]
}
