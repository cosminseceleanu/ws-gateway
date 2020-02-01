package functional.gateway

import java.util.UUID

import api.rest.resources.HttpBackendResource
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import common.FunctionalSpec
import common.api.EndpointsClient
import fixtures.{EndpointFixtures, RouteFixtures}
import play.api.http.Status
import play.api.test.Helpers._

class HttpInboundEventsIT extends FunctionalSpec with EndpointsClient {
  private val wiremockServer = new WireMockServer(options().dynamicPort())
  private var backendUrl = ""
  private var wiremockPort = 0
  private var connectedBackendUrl = ""

  override protected def beforeAll(): Unit = {
    wiremockServer.start()
    wiremockPort = wiremockServer.port()
    backendUrl = s"http://localhost:$wiremockPort"
    connectedBackendUrl = s"$backendUrl/connected"
  }

  feature("Inbound events are sent to HTTP backends") {
    scenario("Connected event is sent to HTTP route backends") {
      Given("endpoint with a http backend for connect route and a backend server")
      val path = "/http-inbound-events"
      val routes = Set(
        RouteFixtures.connectResourceWithHttpBackend(HttpBackendResource(connectedBackendUrl)),
        RouteFixtures.defaultResourceWithDefaultHttpBackend,
        RouteFixtures.disconnectResourceWithDefaultHttpBackend
      )
      val endpoint = createAndAssert(EndpointFixtures.withRoutesAndPath(routes, path))

      wiremockServer.stubFor(
        post(urlPathEqualTo("/connected"))
          .willReturn(aResponse().withStatus(Status.NO_CONTENT))
      )

      When("a new ws connection is established")
      val connectionId = UUID.randomUUID().toString
      await(wsClient.connect(wsConnectionUrl(path, connectionId)))

      Then("Http backend should receive a request")
      Thread.sleep(2000)

      wiremockServer.verify(1, postRequestedFor(urlPathEqualTo("/connected")))
    }
  }

  override protected def afterAll(): Unit = {
    wiremockServer.stop()
  }
}
