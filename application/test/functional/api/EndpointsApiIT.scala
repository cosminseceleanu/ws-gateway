package functional.api

import api.rest.resources.{EndpointResource, FilterResource, RouteResource}
import common.{FunctionalSpec, JsonResource}
import play.api.test.Helpers._
import play.mvc.Http.Status
import com.jayway.jsonpath.matchers.JsonPathMatchers._
import domain.model.RouteType
import fixtures.EndpointFixtures
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

class EndpointsApiIT extends FunctionalSpec with JsonResource {
  feature("GET Api") {
    scenario("Existing endpoint") {
      Given("an existing endpoint id")
      val url = s"$api/endpoints/id"

      When("get by id is called")
      val response = await(wsClient.url(url).get())

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
      val response = await(wsClient.url(url).get())

      Then("response has not found status code")
      response.status mustEqual Status.NOT_FOUND
    }
  }

  feature("POST Api") {
    val url = s"$api/endpoints"

    scenario("Create endpoint") {
      Given("new endpoint")
      val initial = EndpointFixtures.fullEndpointResource()

      When("post api is called")
      val response = await(wsClient.url(url)
                             .withHttpHeaders(contentTypeHeader)
                             .post(toJson(initial)))

      Then("endpoint is created")
      response.status mustEqual Status.CREATED
      val created = fromJson(response.body) (EndpointResource.format)

      created.id != null mustEqual true
      created.routes mustEqual initial.routes
      created.filters mustEqual initial.filters
      created.authenticationMode mustEqual initial.authenticationMode
      created.path mustEqual initial.path
    }

    scenario("Invalid request body") {
      Given("Wrong json")
      val json = "{\"foo\": \"bar\"}";

      When("post api is called")
      val response = await(wsClient.url(url)
                             .withHttpHeaders(contentTypeHeader)
                             .post(json))

      Then("response is bad request")
      response.status mustEqual Status.BAD_REQUEST
    }
  }

  feature("DELETE Api") {
    scenario("endpoint is deleted") {
      Given("a new endpoint")

      val initial = EndpointFixtures.fullEndpointResource()
      val url = s"$api/endpoints"
      // @ToDo refactor this call so it can be reused
      val createResponse = await(wsClient.url(url)
                             .withHttpHeaders(contentTypeHeader)
                             .post(toJson(initial)))
      createResponse.status mustEqual Status.CREATED
      val created = fromJson(createResponse.body) (EndpointResource.format)

      When("delete is called")
      val deleteUrl = s"$url/${created.id.get}"
      val response = await(wsClient.url(deleteUrl).delete())

      Then("endpoint is deleted")
      response.status mustEqual Status.NO_CONTENT
    }

    scenario("Endpoint not found") {
      Given("endpoint id")
      val url = s"$api/endpoints/someid"

      When("delete is called")
      val response = await(wsClient.url(url).delete())

      Then("response has not found status code")
      response.status mustEqual Status.NOT_FOUND
    }
  }

  feature("PUT Api") {
    scenario("endpoint path, filters and routes are successfully updated") {
      Given("a new endpoint")

      val initial = EndpointFixtures.fullEndpointResource()
      val url = s"$api/endpoints"
      // @ToDo refactor this call so it can be reused
      val createResponse = await(wsClient.url(url)
                                   .withHttpHeaders(contentTypeHeader)
                                   .post(toJson(initial)))
      createResponse.status mustEqual Status.CREATED
      var endpoint = fromJson(createResponse.body) (EndpointResource.format)

      When("put is called with updated ")
      val expectedFilters = FilterResource()
      val expectedRoutes = EndpointFixtures.defaultRoutes + RouteResource(RouteType.CUSTOM.toString, "Some name")
      val expectedPath = "/some-new-path"
      endpoint = endpoint.copy(path = expectedPath, filters = expectedFilters, routes = expectedRoutes)

      val response = await(wsClient.url(s"$url/${endpoint.id.get}")
                             .withHttpHeaders(contentTypeHeader)
                             .put(toJson(endpoint)))

      Then("endpoint is deleted")
      response.status mustEqual Status.OK
      val result = fromJson(response.body) (EndpointResource.format)

      result.filters mustEqual expectedFilters
      result.routes mustEqual expectedRoutes
      result.path mustEqual expectedPath
      result.id mustEqual endpoint.id
    }

    scenario("Endpoint not found") {
      Given("endpoint id")
      val url = s"$api/endpoints/someid"
      val initial = EndpointFixtures.fullEndpointResource()

      When("delete is called")
      val response = await(wsClient.url(url)
                             .withHttpHeaders(contentTypeHeader)
                             .put(toJson(initial)))

      Then("response has not found status code")
      response.status mustEqual Status.NOT_FOUND
    }
  }
}
