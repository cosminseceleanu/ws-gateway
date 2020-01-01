package api.rest.assemblers

import api.rest.resources.{EndpointResource, FilterResource, RouteResource}
import common.UnitSpec
import domain.model.{AuthenticationMode, Route, Filter}

class EndpointAssemblerSpec extends UnitSpec {
  private val filterAssemblerMock = mock[FilterAssembler]
  private val routeAssemblerMock = mock[RouteAssembler]
  private val subject = new EndpointAssembler(filterAssemblerMock, routeAssemblerMock)

  "resource to model" when {
    "when resource is correct" should {
      "success" in {
        val routes = Set(RouteResource("type", "name"))
        val filter = FilterResource()
        val resource = EndpointResource(Some("a"), "/a", filter, routes, AuthenticationMode.NONE.toString)
        val expectedFilter = Filter.blacklistHosts(Set("local"))

        (routeAssemblerMock.toModelsSet _).expects(routes).returning(Set(Route.default()))
        (filterAssemblerMock.toModel _).expects(*).returning(Set(expectedFilter))
        val result = subject.toModel(resource)

        result.routes must contain(Route.default())
        result.filters must contain(expectedFilter)
        result.authenticationMode mustEqual(AuthenticationMode.NONE)
        result.id mustEqual("a")
        result.path mustEqual("/a")
      }
    }
  }

  "resource to model" when {
    "when resource has invalid authentication mode" should {
      "throw an exception" in {
        val resource = EndpointResource(Some("a"), "/a", FilterResource(), Set.empty, "aaaa")
        (routeAssemblerMock.toModelsSet _).expects(*).returning(Set(Route.default()))
        (filterAssemblerMock.toModel _).expects(*).returning(Set.empty)

        an[NoSuchElementException] must be thrownBy(subject.toModel(resource))
      }
    }
  }
}
