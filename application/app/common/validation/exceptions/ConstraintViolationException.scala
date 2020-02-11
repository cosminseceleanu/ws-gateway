package common.validation.exceptions

import common.validation.ValidationError

final case class ConstraintViolationException(errors: Set[ValidationError]) extends Exception {
  override def getMessage: String = errors.toString()
}
