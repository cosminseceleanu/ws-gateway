package common

final case class WsConnectionException(
                                private val httpCode: Int,
                                private val message: String = "",
                                private val cause: Throwable = None.orNull
                              ) extends Exception(message, cause) {
  def getHttpCode(): Int = httpCode
}
