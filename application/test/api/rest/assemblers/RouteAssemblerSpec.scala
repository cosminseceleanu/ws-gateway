package api.rest.assemblers

import api.rest.resources.RouteResource
import common.UnitSpec
import domain.model.{Route, RouteType}

class RouteAssemblerSpec extends UnitSpec {
  private val subject = new RouteAssembler()

  "resource to model" when {
    "resource is correct" should {
      "success" in {
        val routeName = "Default route"
        val resource = RouteResource(RouteType.DEFAULT.toString, routeName)
        val result = subject.toModel(resource)

        result.routeType mustEqual(RouteType.DEFAULT)
        result.name mustEqual(routeName)
      }
    }
  }

  "resource to model" when {
    "resource has unknown route type" should {
      "throw an exception" in {
        val resource = RouteResource("no-such-route", "aa")

        an[NoSuchElementException] must be thrownBy(subject.toModel(resource))
      }
    }
  }

  "model to resource" in {
    val route = Route.connect();
    val result = subject.toResource(route);

    result.routeType mustEqual("CONNECT")
    result.name mustEqual(route.name)
  }
}
