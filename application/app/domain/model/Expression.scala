package domain.model

import com.jayway.jsonpath
import com.jayway.jsonpath.JsonPath

sealed trait Expression[+R] {
  def evaluate(): R
}

case class LiteralExpression[+R](input: R) extends Expression[R] {
  override def evaluate(): R = input
}

case class StringExpression(s: String) extends Expression[String] {
  override def evaluate(): String = s
}

case class And(left: Expression[Boolean], right: Expression[Boolean]) extends Expression[Boolean] {
  override def evaluate(): Boolean = left.evaluate() && right.evaluate()
}

case class Or(left: Expression[Boolean], right: Expression[Boolean]) extends Expression[Boolean] {
  override def evaluate(): Boolean = left.evaluate() || right.evaluate()
}

case class Equal[+R](left: Expression[R], right: Expression[R]) extends Expression[Boolean] {
  override def evaluate(): Boolean = left.evaluate() == right.evaluate()
}

case class Matches[+R](target: Expression[String], regex: String) extends Expression[Boolean] {
  override def evaluate(): Boolean = target.evaluate().matches(regex)
}

case class JsonPath(jsonPath: String, jsonProvider: () => String) extends Expression[String] {
  override def evaluate(): String = jsonpath.JsonPath.read[String](jsonProvider(), jsonPath)
}
