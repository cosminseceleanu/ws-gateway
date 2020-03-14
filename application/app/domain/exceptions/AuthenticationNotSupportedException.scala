package domain.exceptions

final case class AuthenticationNotSupportedException(
                                            private val message: String = "",
                                            private val cause: Throwable = None.orNull
                                          ) extends Exception(message, cause)
