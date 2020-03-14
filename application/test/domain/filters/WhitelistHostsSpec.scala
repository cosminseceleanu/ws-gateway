package domain.filters

import common.UnitSpec
import domain.model.Filter

class WhitelistHostsSpec extends UnitSpec {
  "Whitelist hosts filter" when {
    val host = "localhost"
    val filter = Filter.whitelistHosts(Set(host))

    "headers contains a whitelisted host" should  {
      val headers = Map("Host" -> host)
      "request is allowed" in {
        filter.isAllowed(headers) mustBe true
      }
    }

    "headers does not contain a whitelisted host" should  {
      val headers = Map("Host" -> "example.com")
      "request is not allowed" in {
        filter.isAllowed(headers) mustBe false
      }
    }

    "headers map does not contain host header" should  {
      val headers = Map.empty[String, String]
      "request is not allowed" in {
        filter.isAllowed(headers) mustBe false
      }
    }
  }
}
