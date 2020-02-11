package common.validation

import common.validation.exceptions.ConstraintViolationException

trait Validatable {
  private lazy val validator = ScalaValidatorFactory.validator

  def validate[T](subject: T): Unit = {
    val violations = getViolations(subject)
    if (violations.nonEmpty) {
      throw ConstraintViolationException(violations)
    }
  }

  def getViolations[T](subject: T): Set[ValidationError] = validator.validate(subject)
    .map(violation => ValidationError(
      violation.getMessage,
      violation.getPropertyPath.toString,
      violation.getInvalidValue,
      violation.getRootBeanClass.getName
    ))
}
