package api.rest.assemblers

import api.rest.resources.RouteResource
import common.UnitSpec
import domain.model.{Route, RouteType}
import fixtures.{BackendFixtures, RouteFixtures}
import org.scalatest.Matchers._

class RouteAssemblerSpec extends UnitSpec {
  private val subject = new RouteAssembler()

  "resource to model" when {
    "resource does not have backends" should {
      "transform all properties correctly" in {
        val resource = RouteFixtures.defaultResource
        val result = subject.toModel(resource)

        result.routeType mustEqual RouteType.DEFAULT
        result.name mustEqual resource.name
        result.backends should contain(BackendFixtures.debugBackend)
      }
    }

    "resource has backends" should {
      "transform all properties correctly" in {
        val resource = RouteFixtures.defaultResourceWithBackends
        val result = subject.toModel(resource)

        result.routeType mustEqual RouteType.DEFAULT
        result.name mustEqual resource.name
        result.backends should contain(BackendFixtures.httpBackend)
        result.backends should contain(BackendFixtures.kafkaBackend)
      }
    }

    "resource has unknown route type" should {
      "throw an exception" in {
        val resource = RouteResource("no-such-route", "aa")

        an[NoSuchElementException] must be thrownBy subject.toModel(resource)
      }
    }
  }

  "model to resource" when {
    "model does not have backends" should {
      "transform all properties correctly" in {
        val route = Route.connect()
        val result = subject.toResource(route)

        result.routeType mustEqual "CONNECT"
        result.name mustEqual route.name
      }
    }

    "model with http backend" should {
      "transform all properties correctly" in {
        val route = Route.connect(Set(BackendFixtures.httpBackend))
        val result = subject.toResource(route)

        result.routeType mustEqual "CONNECT"
        result.name mustEqual route.name
        result.http should contain(BackendFixtures.httpBackendResource)
      }
    }
  }
}
