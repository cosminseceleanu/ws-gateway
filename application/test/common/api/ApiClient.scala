package common.api

import play.api.libs.ws.WSClient

trait ApiClient {
  protected val contentTypeHeader: (String, String) = ("Content-Type", "application/json")

  val httpClient: WSClient
  val api: String
}
