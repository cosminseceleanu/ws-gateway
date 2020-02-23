package common.rest.errors

import play.api.libs.json.{Json, OFormat}

case class ErrorResource(errorType: String, message: String, errors: List[String], timestamp: Long)

object ErrorResource {
  implicit val format: OFormat[ErrorResource] = Json.format[ErrorResource]

  def apply(errorType: String, message: String): ErrorResource = {
    new ErrorResource(errorType, message, List.empty, System.currentTimeMillis())
  }

  def apply(errorType: String, message: String, errors: List[String]): ErrorResource = {
    new ErrorResource(errorType, message, errors, System.currentTimeMillis())
  }
}
