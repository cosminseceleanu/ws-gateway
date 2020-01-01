package common.rest

import play.api.libs.json.{JsValue, Json, OFormat}

case class ErrorResource(message: String) {
  def toJson(): JsValue = Json.toJson(this)
}

object ErrorResource {
  implicit val format: OFormat[ErrorResource] = Json.format[ErrorResource]
}
