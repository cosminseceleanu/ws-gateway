package domain.exceptions

case class AuthenticationException(
                                  private val message: String = "Wrong credentials!",
                                  private val cause: Throwable = None.orNull
                                ) extends Exception(message, cause)
