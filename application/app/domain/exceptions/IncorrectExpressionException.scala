package domain.exceptions

class IncorrectExpressionException(
                                    private val message: String = "",
                                    private val cause: Throwable = None.orNull
                                  ) extends Exception(message, cause)
