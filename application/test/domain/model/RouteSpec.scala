package domain.model

import org.scalatest.Matchers._
import common.UnitSpec
import domain.model.Expression.Equal
import domain.validation.validators.ExpressionByRouteTypeValidator

class RouteSpec extends UnitSpec {
  "Route canEqual" when {
    val route = Route.connect()

    "compared with other instance type" should {
      "return false" in {
        route.canEqual(AuthenticationMode.NONE) mustBe false
      }
    }

    "compared with same instance type" should {
      "return true" in {
        route.canEqual(Route.disconnect()) mustBe true
      }
    }
  }

  "Route equals" when {
    val route = Route.connect()

    "compared with a route with same values but different instance" should {
      "return true" in {
        val route1 = Route.connect()
        route.equals(route1) mustBe true
      }
    }

    "compared with a route with same type but different name" should {
      "return false" in {
        val r1 = Route(RouteType.CUSTOM, "R1")
        val r2 = Route(RouteType.CUSTOM, "R2")
        r1.equals(r2) mustBe false
      }
    }

    "compared with a route with same name but different type" should {
      "return false" in {
        val r1 = Route(RouteType.DEFAULT, "R1")
        val r2 = Route(RouteType.CUSTOM, "R2")
        r1.equals(r2) mustBe false
      }
    }
  }

  "Route hashCode" when {
    "two routes are equals" should {
      "have same hashcode" in {
        val r1 = Route(RouteType.CUSTOM, "R1")
        val r2 = Route(RouteType.CUSTOM, "R1")

        r1.equals(r2) mustBe true
        r1.hashCode() mustEqual r2.hashCode()
      }
    }

    "any route" should {
      "have hashcode consistent" in {
        val route = Route.connect()
        val hash1 = route.hashCode()

        hash1 mustEqual route.hashCode()
      }
    }
  }

  "Route validation" when {
    "name is blank" should {
      "not be valid" in {
        val route = Route(RouteType.DEFAULT, "1")
        val violations = route.getViolations(route)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "name"
      }
    }

    "name is null" should {
      "not be valid" in {
        val route = Route(RouteType.DEFAULT, None.orNull)
        val violations = route.getViolations(route)

        violations.size mustEqual 1
      }
    }

    "expression is null for custom route" should {
      "not be valid" in {
        val route = Route(RouteType.CUSTOM, "name")
        val violations = route.getViolations(route)

        violations.size mustEqual 1
        violations.head.message mustEqual ExpressionByRouteTypeValidator.MISSING_EXPRESSION_MESSAGE
      }
    }

    "expression is defined for default route" should {
      "not be valid" in {
        val route = Route(RouteType.DEFAULT, "name", Set.empty, Some(Equal("a", "a")))
        val violations = route.getViolations(route)

        violations.size mustEqual 1
        violations.head.message mustEqual ExpressionByRouteTypeValidator.EXPRESSION_SET_FOR_NON_CUSTOM_ROUTE_MESSAGE
      }
    }

    "has more than 10 backends" should {
      "not be valid" in {
        val backends: Set[Backend[_ <: BackendSettings]] = (1 to 11).map(i => KafkaBackend(s"topic.$i")).toSet
        val route = Route(RouteType.DEFAULT, "name", backends)
        val violations = route.getViolations(route)

        violations.size mustEqual 1
        violations.head.message mustEqual "size must be between 0 and 10"
        violations.head.propertyPath mustEqual "backends"
      }
    }
  }
}
