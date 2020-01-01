package domain.model

import common.UnitSpec

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
}
