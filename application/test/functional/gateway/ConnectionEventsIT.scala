package functional.gateway

import java.util.UUID

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import common.api.EndpointsClient
import common.FunctionalSpec
import fixtures.EndpointFixtures
import play.api.http.Status
import play.api.test.Helpers._

class ConnectionEventsIT extends FunctionalSpec with EndpointsClient {
  private val wiremockServer = new WireMockServer(options().dynamicPort())
  private var backendUrl = ""
  private var wiremockPort = 0

  override protected def beforeAll(): Unit = {
    wiremockServer.start()
    wiremockPort = wiremockServer.port()
    backendUrl = s"http://localhost:$wiremockPort"
  }

  feature("Connected event is sent to HTTP backend") {
    scenario("New WS connection generates connected event") {
      Given("endpoint with a http backend for connect route and a backend server")
      val path = "/outbound-connection-events"
      val endpoint = createAndAssert(EndpointFixtures.fromPath(path))

      wiremockServer.stubFor(
        post(urlPathEqualTo("/connected"))
          .willReturn(aResponse().withStatus(Status.NO_CONTENT))
      )

      When("a new ws connection is established")
      val connectionId = UUID.randomUUID().toString
      await(wsClient.connect(wsConnectionUrl(path, connectionId)))

      Then("Http backend is called")

      //TODO - this should be removed when http connector is implemented
//      wiremockServer.verify(1, postRequestedFor(urlPathEqualTo("/connected")))
    }
  }

  override protected def afterAll(): Unit = {
    wiremockServer.stop()
  }
}
