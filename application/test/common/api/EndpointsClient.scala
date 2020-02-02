package common.api

import api.rest.resources.EndpointResource
import common.rest.JsonSupport
import org.scalatest.MustMatchers
import play.api.libs.ws.WSResponse
import play.api.test.Helpers.await
import play.mvc.Http.Status
import play.api.test.Helpers._

trait EndpointsClient extends ApiClient with JsonSupport with MustMatchers {
  val endpointsUrl = s"$api/endpoints"

  def getEndpoint(id: String): WSResponse = {
    await(httpClient.url(s"$endpointsUrl/$id")
      .withHttpHeaders(contentTypeHeader)
      .get())
  }

  def update(id: String, endpoint: EndpointResource): WSResponse = {
    await(httpClient.url(s"$endpointsUrl/$id")
      .withHttpHeaders(contentTypeHeader)
      .put(toJson(endpoint)))
  }

  def createAndAssert(resource: EndpointResource): EndpointResource = {
    val createResponse = create(resource)
    createResponse.status mustEqual Status.CREATED

    fromJson(createResponse.body) (EndpointResource.format)
  }

  def create(resource: EndpointResource): WSResponse = {
    await(httpClient.url(endpointsUrl)
      .withHttpHeaders(contentTypeHeader)
      .post(toJson(resource)))
  }
}
