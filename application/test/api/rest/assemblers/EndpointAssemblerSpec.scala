package api.rest.assemblers

import api.rest.resources.{EndpointResource, FilterResource, RouteResource}
import common.UnitSpec
import domain.model.{AuthenticationMode, Endpoint, EndpointConfiguration, Filter, Route}
import fixtures.{EndpointFixtures, RouteFixtures}

class EndpointAssemblerSpec extends UnitSpec {
  private val filterAssemblerMock = mock[FilterAssembler]
  private val routeAssemblerMock = mock[RouteAssembler]
  private val subject = new EndpointAssembler(filterAssemblerMock, routeAssemblerMock)

  "resource to model" when {
    "resource is correct" should {
      "success" in {
        val routes = Set(RouteResource("type", "name"))
        val filter = FilterResource()
        val resource = EndpointResource(Some("a"), "/a", None, None, filter, routes, AuthenticationMode.NONE.toString)
        val expectedFilter = Filter.blacklistHosts(Set("local"))

        (routeAssemblerMock.toModelsSet _).expects(routes).returning(Set(Route.default()))
        (filterAssemblerMock.toModel _).expects(*).returning(Set(expectedFilter))
        val result = subject.toModel(resource)

        result.routes must contain(Route.default())
        result.filters must contain(expectedFilter)
        result.authenticationMode mustEqual(AuthenticationMode.NONE)
        result.id mustEqual("a")
        result.path mustEqual("/a")
        result.backendParallelism mustEqual EndpointConfiguration.DEFAULT_BACKEND_PARALLELISM
        result.bufferSize mustEqual EndpointConfiguration.DEFAULT_BUFFER_SIZE
      }
    }

    "bufferSize is present" should {
      "bet set in model" in {
        val resource = EndpointFixtures.fullEndpointResource().copy(bufferSize = Some(12))
        (routeAssemblerMock.toModelsSet _).expects(*).returning(Set(Route.default()))
        (filterAssemblerMock.toModel _).expects(*).returning(Set.empty)
        val result = subject.toModel(resource)
        result.bufferSize mustEqual 12
      }
    }

    "backendParallelism is present" should {
      "bet set in model" in {
        val resource = EndpointFixtures.fullEndpointResource().copy(backendParallelism = Some(2))
        (routeAssemblerMock.toModelsSet _).expects(*).returning(Set(Route.default()))
        (filterAssemblerMock.toModel _).expects(*).returning(Set.empty)
        val result = subject.toModel(resource)
        result.backendParallelism mustEqual 2
      }
    }
  }

  "resource has invalid authentication mode" should {
    "throw an exception" in {
      val resource = EndpointResource(Some("a"), "/a", None, None, FilterResource(), Set.empty, "aaaa")
      (routeAssemblerMock.toModelsSet _).expects(*).returning(Set(Route.default()))
      (filterAssemblerMock.toModel _).expects(*).returning(Set.empty)

      an[NoSuchElementException] must be thrownBy(subject.toModel(resource))
    }
  }

  "model to resource" when {
    "basic model" should {
      "resource should be assembled" in {
        val model = Endpoint("id", "path")
        val routes = Set(RouteFixtures.defaultResource)

        (routeAssemblerMock.toResourcesSet _).expects(*).returning(routes)
        (filterAssemblerMock.toResource _).expects(*).returning(FilterResource())

        val resource = subject.toResource(model)

        resource.id.isDefined mustEqual true
        resource.id.get mustEqual "id"
        resource.path mustEqual "path"
        resource.routes mustEqual routes
        resource.filters mustEqual FilterResource()
      }
    }
  }
}
