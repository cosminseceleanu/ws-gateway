package domain.model

import com.jayway.jsonpath.JsonPath

sealed trait Expression[+T] {
  val name: String
  def evaluate(json: String): T
}

sealed trait TerminalExpression[+T, +R] extends Expression[T] {
  val path: String
  val value: R

  def getValueForPath(json: String): R = JsonPath.read[R](json, path)
}

sealed trait BooleanExpression extends Expression[Boolean] {
  val left: Expression[Boolean]
  val right: Expression[Boolean]
}

object Expression {
  val AND = "and"
  val OR = "or"
  val EQUAL = "equal"
  val MATCHES = "matches"

  private val ALL = Set(AND, OR, EQUAL, MATCHES)
  private val BOOLEAN = Set(AND, OR)

  def isSupported(name: String): Boolean = ALL.contains(name)
  def isBoolean(name: String): Boolean = BOOLEAN.contains(name)

  case class Equal[+R](path: String, value: R) extends TerminalExpression[Boolean, R] {
    override val name: String = EQUAL
    override def evaluate(json: String): Boolean = getValueForPath(json) == value
  }

  case class Matches(path: String, value: String) extends TerminalExpression[Boolean, String] {
    override val name: String = MATCHES
    override def evaluate(json: String): Boolean = getValueForPath(json).matches(value)
  }

  case class And(left: Expression[Boolean], right: Expression[Boolean]) extends BooleanExpression {
    override val name: String = AND
    override def evaluate(json: String): Boolean = left.evaluate(json) && right.evaluate(json)
  }

  case class Or(left: Expression[Boolean], right: Expression[Boolean]) extends BooleanExpression {
    override val name: String = OR
    override def evaluate(json: String): Boolean = left.evaluate(json) || right.evaluate(json)
  }
}

