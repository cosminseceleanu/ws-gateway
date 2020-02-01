package api.rest.resources

import play.api.libs.json.{Json, OFormat}

case class KafkaBackendResource(topic: String)

object KafkaBackendResource {
  implicit val format: OFormat[KafkaBackendResource] = Json.format[KafkaBackendResource]
}
