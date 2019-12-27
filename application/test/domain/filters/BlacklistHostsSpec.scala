package domain.filters

import common.UnitSpec
import domain.model.Filter

class BlacklistHostsSpec extends UnitSpec {
  "Blacklist hosts filter" when {
    val host = "localhost"
    val filter = Filter.blacklistHosts(Set(host))

    "headers contains a blacklisted host" should  {
      val headers = Map("host" -> host)
      "request is not allowed" in {
        filter.isAllowed(headers) mustBe false
      }
    }

    "headers does not contain a blacklisted host" should  {
      val headers = Map("host" -> "example.com")
      "request is allowed" in {
        filter.isAllowed(headers) mustBe true
      }
    }

    "headers map does not contain a host header" should  {
      val headers = Map.empty[String, String]
      "request is allowed" in {
        filter.isAllowed(headers) mustBe true
      }
    }
  }
}
