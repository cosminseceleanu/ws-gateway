package domain.exceptions

final case class ExpressionNotSupportedException(name: String) extends IncorrectExpressionException(s"Expression $name is not supported")
