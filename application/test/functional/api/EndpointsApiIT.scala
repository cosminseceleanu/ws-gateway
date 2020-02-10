package functional.api

import org.scalatest.Matchers._
import api.rest.resources.{EndpointResource, FilterResource, RouteResource}
import com.jayway.jsonpath.matchers.JsonPathMatchers._
import common.FunctionalSpec
import common.api.EndpointsClient
import domain.model.RouteType
import fixtures.{BackendFixtures, EndpointFixtures, ExpressionFixtures, RouteFixtures}
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import play.api.test.Helpers._
import play.mvc.Http.Status

class EndpointsApiIT extends FunctionalSpec with EndpointsClient {
  feature("GET Api") {
    scenario("Existing endpoint") {
      Given("an existing endpoint id")
      When("get by id is called")
      val response = getEndpoint("id")

      Then("json response contains endpoint")
      response.status mustEqual Status.OK
      val body: String = response.body

      assertThat(body, isJson)
      hasJsonPath("$.path", equalTo("demo"))
      hasJsonPath("$.id", equalTo("id"))
      hasJsonPath("$.routes[*]", equalTo("3"))
      hasJsonPath("$.filters")
    }

    scenario("Endpoint not found") {
      Given("endpoint id")
      val url = s"$api/endpoints/someid"

      When("get by id is called")
      val response = await(httpClient.url(url).get())

      Then("response has not found status code")
      response.status mustEqual Status.NOT_FOUND
    }
  }

  feature("POST Api") {
    val url = s"$api/endpoints"

    scenario("Create endpoint with http backends") {
      Given("new endpoint with http backends for default routes")
      val routes = Set(
        RouteFixtures.connectResourceWithDefaultHttpBackend,
        RouteFixtures.defaultResourceWithDefaultHttpBackend,
        RouteFixtures.disconnectResourceWithDefaultHttpBackend,
      )
      val initial = EndpointFixtures.withRoutes(routes)

      When("post api is called")
      val created = createAndAssert(initial)

      Then("endpoint is created")

      created.id != null mustEqual true
      created.routes mustEqual initial.routes
      created.httpBackends should have size 3
      created.httpBackends should contain(BackendFixtures.httpBackendResource)
    }

    scenario("Create endpoint with custom route") {
      Given("new endpoint with custom route")
      val initialCustomRoute = RouteFixtures.customResourceWithDefaultHttpBackend("Test custom route", ExpressionFixtures.nextedExpression)
      val routes = EndpointFixtures.defaultRoutes + initialCustomRoute
      val initial = EndpointFixtures.withRoutes(routes)

      When("post api is called")
      val created = createAndAssert(initial)

      Then("endpoint is created and has a custom route with expression")
      val customRoute = created.routes.find(_.routeType == RouteType.CUSTOM.toString)

      created.id != null mustEqual true
      customRoute.isDefined mustEqual true
      customRoute.get.expression.isDefined mustEqual true
      customRoute.get.expression.get mustEqual ExpressionFixtures.nextedExpression
    }

    scenario("Create endpoint") {
      Given("new endpoint")
      val initial = EndpointFixtures.fullEndpointResource()

      When("post api is called")
      val created = createAndAssert(initial)

      Then("endpoint is created")

      created.id != null mustEqual true
      created.routes mustEqual initial.routes
      created.filters mustEqual initial.filters
      created.authenticationMode mustEqual initial.authenticationMode
      created.path mustEqual initial.path
    }

    scenario("Invalid request body") {
      Given("Wrong json")
      val json = "{\"foo\": \"bar\"}"

      When("post api is called")
      val response = await(httpClient.url(url).withHttpHeaders(contentTypeHeader).post(json))

      Then("response is bad request")
      response.status mustEqual Status.BAD_REQUEST
    }
  }

  feature("DELETE Api") {
    scenario("endpoint is deleted") {
      Given("a new endpoint")
      val initial = EndpointFixtures.fullEndpointResource()
      val created = createAndAssert(initial)

      When("delete is called")
      val deleteUrl = s"$endpointsUrl/${created.id.get}"
      val response = await(httpClient.url(deleteUrl).delete())

      Then("endpoint is deleted")
      response.status mustEqual Status.NO_CONTENT
    }

    scenario("Endpoint not found") {
      Given("endpoint id")
      val url = s"$api/endpoints/someid"

      When("delete is called")
      val response = await(httpClient.url(url).delete())

      Then("response has not found status code")
      response.status mustEqual Status.NOT_FOUND
    }
  }

  feature("PUT Api") {
    scenario("endpoint path, filters and routes are successfully updated") {
      Given("a new endpoint")
      var endpoint = createAndAssert(EndpointFixtures.fullEndpointResource())

      When("put is called with updated ")
      val expectedFilters = FilterResource()
      val expectedRoutes = EndpointFixtures.defaultRoutes + RouteResource(RouteType.CUSTOM.toString, "Some name")
      val expectedPath = "/some-new-path"
      endpoint = endpoint.copy(path = expectedPath, filters = expectedFilters, routes = expectedRoutes)
      val response = update(endpoint.id.get, endpoint)

      response.status mustEqual Status.OK

      val result = fromJson(response.body) (EndpointResource.format)

      Then("endpoint is updated")
      result.filters mustEqual expectedFilters
      result.routes mustEqual expectedRoutes
      result.path mustEqual expectedPath
      result.id mustEqual endpoint.id
    }

    scenario("Endpoint not found") {
      Given("endpoint id")
      When("put is called")
      val response = update("someId", EndpointFixtures.fullEndpointResource())

      Then("response has not found status code")
      response.status mustEqual Status.NOT_FOUND
    }
  }
}
