package functional.gateway

import java.util.concurrent.TimeUnit

import api.rest.resources.EndpointResource
import common.{FunctionalSpec, JsonResource, WsConnectionException}
import fixtures.EndpointFixtures
import play.api.test.Helpers._
import play.mvc.Http.Status

class GatewayDynamicPathsIT extends FunctionalSpec with JsonResource {
  val defaultTimeout = 1000

  feature("WS Gateway dynamic paths") {
    scenario("Request to a path that is not defined in routes file is handled by WSFrontController") {
      Given("path that is not defined in routes")
      val path = "/test/s/2/d"
      val endpoint = EndpointFixtures.fromPath(path)
      createEndpoint(endpoint)

      When("try to connect")
      val (in, out) = await(wsClient.connect(s"$wsHost$path"))

      Then("Connection is established and accepts messages")
      await(out.offer("Hi"), defaultTimeout, TimeUnit.MILLISECONDS)
    }

    scenario("No endpoint defined to match the requested path") {
      Given("path that is not catched by an endpoint")
      val path = "/ssss/s/2/d"

      When("try to connect")
      Then("404 is returned")
      val caught = intercept[WsConnectionException] {
        await(wsClient.connect(s"$wsHost$path"))
      }

      caught.getHttpCode() mustEqual Status.NOT_FOUND
    }
  }

  private def createEndpoint(initial: EndpointResource) = {
    val createResponse = await(httpClient.url(endpointsUrl)
                                 .withHttpHeaders(contentTypeHeader)
                                 .post(toJson(initial)))
    createResponse.status mustEqual Status.CREATED

    fromJson(createResponse.body) (EndpointResource.format)
  }
}
