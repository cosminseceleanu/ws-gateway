package domain.exceptions

final case class IncorrectBooleanExpressionException(private val message: String = "") extends IncorrectExpressionException(message)
