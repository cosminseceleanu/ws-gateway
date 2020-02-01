package api.rest.resources

import play.api.libs.json.{Json, OFormat}

case class HttpBackendResource(destination: String, additionalHeaders: Map[String, String], timeout: Int)

object HttpBackendResource {
  private val DEFAULT_TIMEOUT = 200

  implicit val format: OFormat[HttpBackendResource] = Json.format[HttpBackendResource]

  def apply(destination: String): HttpBackendResource = new HttpBackendResource(destination, Map.empty, DEFAULT_TIMEOUT)

  def apply(destination: String, timeout: Int): HttpBackendResource = new HttpBackendResource(destination, Map.empty, timeout)

  def apply(destination: String, additionalHeaders: Map[String, String]): HttpBackendResource = {
    new HttpBackendResource(destination, additionalHeaders, DEFAULT_TIMEOUT)
  }

  def apply(destination: String, additionalHeaders: Map[String, String], timeout: Int): HttpBackendResource = {
    new HttpBackendResource(destination, additionalHeaders, timeout)
  }
}
