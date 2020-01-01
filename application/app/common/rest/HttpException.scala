package common.rest

import play.mvc.Http.Status

final case class HttpException(
                                private val httpCode: Int = Status.INTERNAL_SERVER_ERROR,
                                private val message: String = "",
                                private val cause: Throwable = None.orNull
                              ) extends Exception(message, cause) {
  def getHttpCode(): Int = httpCode
}
