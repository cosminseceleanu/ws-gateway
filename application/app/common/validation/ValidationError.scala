package common.validation

case class ValidationError(
                          message: String,
                          propertyPath: String,
                          invalidValue: Object,
                          beanClassName: String
                          )
