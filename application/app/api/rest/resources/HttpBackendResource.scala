package api.rest.resources

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class HttpBackendResource(destination: String, additionalHeaders: Map[String, String], timeoutInMillis: Int)

object HttpBackendResource {
  private val DEFAULT_TIMEOUT = 200

  implicit val writes: Writes[HttpBackendResource] = Json.writes[HttpBackendResource]
  implicit val reads: Reads[HttpBackendResource] = (
    (JsPath \ "destination").read[String] and
    (JsPath \ "additionalHeaders").readNullable[Map[String, String]] and
    (JsPath \ "timeoutInMillis").readNullable[Int]
    )((destination, additionalHeaders, timeoutInMillis) => {
    HttpBackendResource(destination, additionalHeaders.getOrElse(Map.empty), timeoutInMillis.getOrElse(DEFAULT_TIMEOUT))
  })

  def apply(destination: String): HttpBackendResource = new HttpBackendResource(destination, Map.empty, DEFAULT_TIMEOUT)

  def apply(destination: String, timeoutInMillis: Int): HttpBackendResource = new HttpBackendResource(destination, Map.empty, timeoutInMillis)

  def apply(destination: String, additionalHeaders: Map[String, String]): HttpBackendResource = {
    new HttpBackendResource(destination, additionalHeaders, DEFAULT_TIMEOUT)
  }

  def apply(destination: String, additionalHeaders: Map[String, String], timeoutInMillis: Int): HttpBackendResource = {
    new HttpBackendResource(destination, additionalHeaders, timeoutInMillis)
  }
}
