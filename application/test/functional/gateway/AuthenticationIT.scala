package functional.gateway

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import api.rest.resources.AuthenticationResource
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import common.{FunctionalSpec, WsConnectionException}
import common.api.EndpointsClient
import fixtures.EndpointFixtures
import play.api.test.Helpers._
import play.mvc.Http.Status

import scala.collection.immutable

class AuthenticationIT extends FunctionalSpec with EndpointsClient {
  private implicit val defaultTimeout: Int = 10000

  private val wiremockServer = new WireMockServer(options().dynamicPort())
  private var wiremockPort = 0
  private var authorizationServerUrl = ""

  override protected def beforeAll(): Unit = {
    wiremockServer.start()
    wiremockPort = wiremockServer.port()
    authorizationServerUrl = s"http://localhost:$wiremockPort/verify_token"
  }


  feature("Gateway Basic Authentication") {
    Given("endpoint who requires basic authentication")
    val path = "/basic-auth"
    val username = "cosmin"
    val password = "St0nGpa$$"
    val endpoint = EndpointFixtures.fromPath(path)
      .copy(authentication = AuthenticationResource.basic(username, password))
    createAndAssert(endpoint)

    scenario("Authorization header is sent with correct credentials") {
      When("Try to connect")
      val (in, out) = await(wsClient.connectWithHeaders(
        s"$wsHost$path",
        immutable.Seq(getBasicAuthorizationHeader(username, password))
      ))

      Then("Access granted")
      await(out.offer("Hi"), defaultTimeout, TimeUnit.MILLISECONDS)
    }

    scenario("Authorization header is sent with wrong credentials") {
      When("Try to connect")
      val caught = intercept[WsConnectionException] {
        await(wsClient.connectWithHeaders(
          s"$wsHost$path",
          immutable.Seq(getBasicAuthorizationHeader("s", "s"))
        ))
      }

      Then("Access denied")
      caught.getHttpCode() mustEqual Status.UNAUTHORIZED
    }
  }

  feature("Gateway Bearer Authentication") {
    scenario("Authorization header is sent with a good token") {
      Given("endpoint who requires bearer authentication")
      val path = "/bearer-auth"
      val endpoint = EndpointFixtures.fromPath(path)
        .copy(authentication = AuthenticationResource.bearer(authorizationServerUrl))
      createAndAssert(endpoint)
      wiremockServer.stubFor(
        post(urlPathEqualTo("/verify_token"))
          .withRequestBody(equalToJson("{\"access_token\": \"good\"}"))
          .willReturn(aResponse().withStatus(Status.OK))
      )
      When("Try to connect")
      val (in, out) = await(wsClient.connectWithHeaders(
        s"$wsHost$path",
        immutable.Seq(getBearerAuthorizationHeader("good"))
      ))

      Then("Access granted")
    }

    scenario("Authorization header is sent with a wrong token") {
      Given("endpoint who requires bearer authentication")
      val path = "/bearer-auth-access-denied"
      val endpoint = EndpointFixtures.fromPath(path)
        .copy(authentication = AuthenticationResource.bearer(authorizationServerUrl))
      createAndAssert(endpoint)

      wiremockServer.stubFor(
        post(urlPathEqualTo("/verify_token"))
          .withRequestBody(equalToJson("{\"access_token\": \"wrong\"}"))
          .willReturn(aResponse().withStatus(Status.FORBIDDEN))
      )

      When("Try to connect")
      val caught = intercept[WsConnectionException] {
        await(wsClient.connectWithHeaders(
          s"$wsHost$path",
          immutable.Seq(getBearerAuthorizationHeader("wrong"))
        ))
      }

      Then("Access denied")
      caught.getHttpCode() mustEqual Status.UNAUTHORIZED
    }
  }

  private def getBearerAuthorizationHeader(token: String) = {
    Authorization(OAuth2BearerToken(token))
  }

  private def getBasicAuthorizationHeader(username: String, password: String) = {
    Authorization(BasicHttpCredentials(username, password))
  }
}
