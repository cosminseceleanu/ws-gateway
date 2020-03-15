package functional.gateway


import java.net.InetAddress

import akka.http.scaladsl.model.RemoteAddress
import akka.http.scaladsl.model.headers.{Host, `X-Forwarded-For`}
import api.rest.resources.FilterResource
import common.api.EndpointsClient
import common.{FunctionalSpec, WsConnectionException}
import fixtures.EndpointFixtures
import play.api.test.Helpers._
import play.mvc.Http.Status

import scala.collection.immutable

class CustomFiltersIT extends FunctionalSpec with EndpointsClient {
  private implicit val defaultTimeout: Int = 10000

  feature("Custom blacklist host filter") {
    Given("endpoint with blacklist hosts filter")
    val path = "/blacklist-host"
    val endpoint = EndpointFixtures.fromPath(path)
      .copy(filters = FilterResource().copy(blacklistHosts = Set("bad-host")))
    createAndAssert(endpoint)

    scenario("Request is made from a blacklisted host") {
      When("Try to connect")
      val caught = intercept[WsConnectionException] {
        await(wsClient.connectWithHeaders(
          s"$wsHost$path",
          immutable.Seq(Host("bad-host"))
        ))
      }

      Then("Access denied")
      caught.getHttpCode() mustEqual Status.FORBIDDEN
    }

    scenario("Request is made from a good host") {
      When("Try to connect")
      await(wsClient.connectWithHeaders(s"$wsHost$path", immutable.Seq(Host("good-host"))))
      Then("Connected successfully")
    }
  }

  feature("Custom blacklist IP filter") {
    Given("endpoint with blacklist IP's filter")
    val path = "/blacklist-ip"
    val endpoint = EndpointFixtures.fromPath(path)
      .copy(filters = FilterResource().copy(blacklistIps = Set("127.0.0.5")))
    createAndAssert(endpoint)

    scenario("Request is made from a blacklisted ip") {
      When("Try to connect")
      val blackIp = RemoteAddress.IP(InetAddress.getByName("127.0.0.5"))

      val caught = intercept[WsConnectionException] {
        await(wsClient.connectWithHeaders(
          s"$wsHost$path",
          immutable.Seq(`X-Forwarded-For`(blackIp))
        ))
      }

      Then("Access denied")
      caught.getHttpCode() mustEqual Status.FORBIDDEN
    }

    scenario("Request is made from a good ip") {
      When("Try to connect")
      val goodIp = RemoteAddress.IP(InetAddress.getByName("127.0.0.1"))
      await(wsClient.connectWithHeaders(s"$wsHost$path", immutable.Seq(`X-Forwarded-For`(goodIp))))
      Then("Connected successfully")
    }
  }
}
