package common.validation

import javax.validation.executable.ExecutableValidator
import javax.validation.metadata.BeanDescriptor
import javax.validation.{ConstraintViolation, Validator}

import scala.collection.JavaConverters._

case class ScalaValidator(validator: Validator) {
  def validate[T](obj: T, groups: Class[_]*): Set[ConstraintViolation[T]] = {
    validator.validate(obj, groups: _*).asScala.toSet
  }

  def validateValue[T](beanType: Class[T], propertyName: String, value: scala.Any, groups: Class[_]*): Set[ConstraintViolation[T]] = {
    validator.validateValue(beanType, propertyName, value, groups: _*).asScala.toSet
  }

  def validateProperty[T](obj: T, propertyName: String, groups: Class[_]*): Set[ConstraintViolation[T]] = {
    validator.validateProperty(obj, propertyName, groups: _*).asScala.toSet
  }

  def unwrap[T](t: Class[T]): T = {
    validator.unwrap(t)
  }

  def forExecutables(): ExecutableValidator = {
    validator.forExecutables()
  }

  def getConstraintsForClass(clazz: Class[_]): BeanDescriptor = {
    validator.getConstraintsForClass(clazz)
  }
}
