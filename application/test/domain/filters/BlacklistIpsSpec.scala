package domain.filters

import common.UnitSpec
import domain.model.Filter

class BlacklistIpsSpec extends UnitSpec {
  "Blacklist ips filter" when {
    val ip = "127.0.0.1"
    val filter = Filter.blacklistIps(Set(ip))

    "headers contains a blacklisted ip" should  {
      val headers = Map("ip" -> ip)
      "request is not allowed" in {
        filter.isAllowed(headers) mustBe false
      }
    }

    "headers does not contain a blacklisted ip" should  {
      val headers = Map("ip" -> "10.1.1.5")
      "request is allowed" in {
        filter.isAllowed(headers) mustBe true
      }
    }

    "headers map does not contain a ip header" should  {
      val headers = Map.empty[String, String]
      "request is allowed" in {
        filter.isAllowed(headers) mustBe true
      }
    }
  }
}
