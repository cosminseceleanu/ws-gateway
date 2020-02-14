package domain.model

import common.UnitSpec
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
  }
}
