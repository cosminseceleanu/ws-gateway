package domain.model

import common.UnitSpec
import domain.model.Expression.Equal
import domain.model.filters.BlacklistHosts

class EndpointConfigurationSpec extends UnitSpec {

  "Endpoint configuration validation" when {
    "when a filter is not valid" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set(BlacklistHosts(None.orNull)), Set.empty)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "filterSet[].blacklist"
      }
    }

    "when filters are null" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(None.orNull, Set.empty)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "filters"
      }
    }

    "when routes are null" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set.empty, None.orNull)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "routes"
      }
    }

    "when routes size is greater than 255" should {
      "should be invalid" in {
        val expression = Some(Equal("a", "b"))
        val routes = (0 to 260).map(i => Route(RouteType.CUSTOM, s"route.$i").copy(expression = expression)).toSet
        val configuration = EndpointConfiguration(Set.empty, routes)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "routes"
        violations.head.message mustEqual "size must be between 0 and 255"
      }
    }

    "when routes contains two connect routes" should {
      "should be invalid" in {
        val routes = Set(Route.connect(), Route.connect().copy(name = "Connect 2"))
        val configuration = EndpointConfiguration(Set.empty, routes)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.message mustEqual "Endpoint can have at most 1 route of type CONNECT"
      }
    }

    "when authentication mode is null" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set.empty, Set.empty).copy(authenticationMode = None.orNull)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "authenticationMode"
        violations.head.message mustEqual "must not be null"
      }
    }

    "when buffer size is greater than 10000" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set.empty, Set.empty).copy(bufferSize = 29999)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "bufferSize"
      }
    }

    "when buffer size is less than 10" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set.empty, Set.empty).copy(bufferSize = 8)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "bufferSize"
      }
    }

    "when createdAt is null" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set.empty, Set.empty).copy(createdAt = None.orNull)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "createdAt"
      }
    }

    "when backendParallelism size is greater than 32" should {
      "should be invalid" in {
        val configuration = EndpointConfiguration(Set.empty, Set.empty).copy(backendParallelism = 50)
        val violations = configuration.getViolations(configuration)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "backendParallelism"
      }
    }
  }
}
