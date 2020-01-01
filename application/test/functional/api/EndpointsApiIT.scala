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
      val url = s"$endpointsUrl/id"

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
      val created = createEndpoint(initial)

      Then("endpoint is created")

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
      val response = await(wsClient.url(url).withHttpHeaders(contentTypeHeader).post(json))

      Then("response is bad request")
      response.status mustEqual Status.BAD_REQUEST
    }
  }

  feature("DELETE Api") {
    scenario("endpoint is deleted") {
      Given("a new endpoint")
      val initial = EndpointFixtures.fullEndpointResource()
      val created = createEndpoint(initial)

      When("delete is called")
      val deleteUrl = s"$endpointsUrl/${created.id.get}"
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
      var endpoint = createEndpoint(EndpointFixtures.fullEndpointResource())

      When("put is called with updated ")
      val expectedFilters = FilterResource()
      val expectedRoutes = EndpointFixtures.defaultRoutes + RouteResource(RouteType.CUSTOM.toString, "Some name")
      val expectedPath = "/some-new-path"
      endpoint = endpoint.copy(path = expectedPath, filters = expectedFilters, routes = expectedRoutes)
      val result = updateEndpoint(endpoint.id.get, endpoint)

      Then("endpoint is updated")
      result.filters mustEqual expectedFilters
      result.routes mustEqual expectedRoutes
      result.path mustEqual expectedPath
      result.id mustEqual endpoint.id
    }

    scenario("Endpoint not found") {
      Given("endpoint id")
      When("put is called")
      val response = executePut("someId", EndpointFixtures.fullEndpointResource())

      Then("response has not found status code")
      response.status mustEqual Status.NOT_FOUND
    }
  }

  private def updateEndpoint(id: String, endpoint: EndpointResource) = {
    val response = executePut(id, endpoint)

    response.status mustEqual Status.OK

    fromJson(response.body) (EndpointResource.format)
  }

  private def executePut(id: String, endpoint: EndpointResource) = {
    await(wsClient.url(s"$endpointsUrl/${id}")
            .withHttpHeaders(contentTypeHeader)
            .put(toJson(endpoint)))
  }

  private def createEndpoint(initial: EndpointResource) = {
    val createResponse = executePost(initial)
    createResponse.status mustEqual Status.CREATED

    fromJson(createResponse.body) (EndpointResource.format)
  }

  private def executePost(initial: EndpointResource) = {
    await(wsClient.url(endpointsUrl)
            .withHttpHeaders(contentTypeHeader)
            .post(toJson(initial)))
  }
}
