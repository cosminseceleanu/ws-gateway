package common.api

import common.rest.JsonSupport
import org.scalatest.MustMatchers
import play.api.libs.ws.WSResponse
import play.api.test.Helpers._
import play.mvc.Http.Status

trait ConnectionsClient extends ApiClient with JsonSupport with MustMatchers {
  val connectionsUrl = s"$api/connections"

  def sendEventAndAssert(connectionId: String, payload: String): Unit = {
    val response = sendEvent(connectionId, payload)
    response.status mustBe Status.NO_CONTENT
  }

  def sendEvent(connectionId: String, payload: String): WSResponse = {
    await(httpClient.url(s"$connectionsUrl/$connectionId")
      .addHttpHeaders(contentTypeHeader)
      .post(payload))
  }
}
