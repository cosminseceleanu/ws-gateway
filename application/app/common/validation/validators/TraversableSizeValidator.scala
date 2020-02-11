package common.validation.validators

import common.validation.constraints.TraversableSize
import javax.validation.{ConstraintValidator, ConstraintValidatorContext}

class TraversableSizeValidator extends ConstraintValidator[TraversableSize, Traversable[_]] {
  private var annotation: TraversableSize = None.orNull

  override def initialize(constraintAnnotation: TraversableSize): Unit = {
    annotation = constraintAnnotation
  }

  override def isValid(value: Traversable[_], context: ConstraintValidatorContext): Boolean = {
    if (value == null) {
      true
    } else {
      value.size >= annotation.min() && value.size <= annotation.max()
    }
  }
}


