package domain.model

import org.scalatest.Matchers._

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

  "Endpoint validation" when {
    "path starts with /api/internal" should {
      "should be invalid" in {
        val endpoint = Endpoint("da", "/api/internal/my-custom-endpoint")
        val violations = endpoint.getViolations(endpoint)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "path"
      }
    }

    "path is null" should {
      "should be invalid" in {
        val endpoint = Endpoint("da", None.orNull)
        val violations = endpoint.getViolations(endpoint)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "path"
      }
    }

    "configuration is null" should {
      "should be invalid" in {
        val endpoint = Endpoint("da", "/some/path", None.orNull)
        val violations = endpoint.getViolations(endpoint)

        violations.size mustEqual 1
        violations.head.propertyPath should include("configuration")
        violations.head.message mustEqual "must not be null"
      }
    }

    "configuration is not valid" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set.empty, None.orNull)
        val endpoint = Endpoint("da", "/some/path", configuration)
        val violations = endpoint.getViolations(endpoint)

        violations.size mustEqual 1
        violations.head.propertyPath should include("configuration.routes")
        violations.head.message mustEqual "must not be null"
      }
    }
  }
}
