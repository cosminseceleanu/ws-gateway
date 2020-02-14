package functional.gateway

import java.util.UUID
import java.util.concurrent.TimeUnit

import common.FunctionalSpec
import common.api.{ConnectionsClient, EndpointsClient}
import fixtures.EndpointFixtures
import play.api.libs.json.Json
import play.api.test.Helpers._


class OutboundEventsFlowIT extends FunctionalSpec with EndpointsClient with ConnectionsClient {
  private val DEFAULT_TIMEOUT = 5
  private val path = "/outbound-flow"

  override protected def beforeAll(): Unit = {
    createAndAssert(EndpointFixtures.fromPath(path))
  }

  feature("WS connection receive events sent by backends through http endpoint") {
    scenario("WS connection receive outbound events") {

      Given("a ws connection")
      val connectionId = UUID.randomUUID().toString
      val receivedMessages = await(wsClient.connect(wsConnectionUrl(path, connectionId)))._1

      When("http request is made to connections api")
      val payload = Json.stringify(Json.obj("foo" -> "bar"))
      sendEvent(connectionId, payload)

      Then("message received by ws connection")
      receivedMessages.poll(DEFAULT_TIMEOUT, TimeUnit.SECONDS) mustBe payload
    }

    scenario("WS connection receive multiple outbound events in the same order") {

      Given("a ws connection")
      val connectionId = UUID.randomUUID().toString
      val receivedMessages = await(wsClient.connect(wsConnectionUrl(path, connectionId)))._1

      When("send multiple http requests to connwections api")
      val payload1 = Json.stringify(Json.obj("foo" -> "bar"))
      val payload2 = Json.stringify(Json.obj("a" -> "b"))
      val payload3 = Json.stringify(Json.obj("id" -> 1, "msg" -> "some msg"))

      sendEvent(connectionId, payload1)
      sendEvent(connectionId, payload2)
      sendEvent(connectionId, payload3)

      Then("message received by ws connection")
      receivedMessages.poll(DEFAULT_TIMEOUT, TimeUnit.SECONDS) mustBe payload1
      receivedMessages.poll(DEFAULT_TIMEOUT, TimeUnit.SECONDS) mustBe payload2
      receivedMessages.poll(DEFAULT_TIMEOUT, TimeUnit.SECONDS) mustBe payload3
    }
  }
}
