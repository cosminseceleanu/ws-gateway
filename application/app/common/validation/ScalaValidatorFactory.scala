package common.validation

import javax.validation.{Validation, ValidatorFactory}

object ScalaValidatorFactory {

  lazy val validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory

  lazy val validator: ScalaValidator = {
    val v = validatorFactory.getValidator
    ScalaValidator(v)
  }
}
