package gateway.backend

final case class InvalidHttpStatusCodeException(
                                            private val status: Int,
                                            private val message: String = s"Invalid http status code",
                                            private val cause: Throwable = None.orNull
                                          ) extends Exception(message, cause)
