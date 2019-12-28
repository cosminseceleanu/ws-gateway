package domain.model

import common.UnitSpec

class EndpointSpec extends UnitSpec {
  "Endpoint without connect route" when {
    "hasRoute is invoked" should {
      "return false" in {
        val endpoint = Endpoint("1", "/p", Set.empty, Set(Route.default()))
        endpoint.hasRoute(RouteType.CONNECT) mustBe false
      }
    }
  }

  "Endpoint with default route" when {
    "hasRoute is invoked" should {
      "return true" in {
        val endpoint = Endpoint("1", "/p", Set.empty, Set(Route.default()))
        endpoint.hasRoute(RouteType.DEFAULT) mustBe true
      }
    }
  }

  "Any endpoint" when {
    "addRoutes is invoked" should {
      "return a new instance" in {
        val endpoint = Endpoint("1", "/p", Set.empty, Set.empty)
        val result = endpoint.addRoutes(Set(Route.connect()))

        endpoint mustNot equal(result)
      }
    }
  }

  "Endpoint with no routes" when {
    "addRoutes is invoked" should {
      "add all new routes" in {
        val initial = Endpoint("1", "/p", Set.empty, Set.empty)
        val endpoint = initial.addRoutes(Set(Route.default(), Route.connect(), Route.disconnect()))

        endpoint.routes must contain(Route.default())
        endpoint.routes must contain(Route.connect())
        endpoint.routes must contain(Route.disconnect())
      }
    }
  }

  "Endpoint with connect route" when {
    "addRoutes is invoked with a new connect route" should {
      "replace old route" in {
        val initial = Endpoint("1", "/p", Set.empty, Set(Route.connect()))
        val endpoint = initial.addRoutes(Set(Route.connect(), Route.disconnect()))

        endpoint.routes must have size(2)
        endpoint.routes must contain(Route.connect())
        endpoint.routes must contain(Route.disconnect())
      }
    }
  }
}
