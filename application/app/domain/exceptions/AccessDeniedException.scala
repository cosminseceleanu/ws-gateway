package domain.exceptions

case class AccessDeniedException(
                                  private val message: String = "Access Denied!",
                                  private val cause: Throwable = None.orNull
                                ) extends Exception(message, cause)
