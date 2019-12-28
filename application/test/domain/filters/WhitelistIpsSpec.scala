package domain.filters

import common.UnitSpec
import domain.model.Filter

class WhitelistIpsSpec extends UnitSpec {
  "Whitelist ips filter" when {
    val ip = "127.0.0.1"
    val filter = Filter.whitelistIps(Set(ip))

    "headers contains a whitelisted ip" should  {
      val headers = Map("ip" -> ip)
      "request is allowed" in {
        filter.isAllowed(headers) mustBe true
      }
    }

    "headers does not contain a whitelisted ip" should  {
      val headers = Map("ip" -> "10.1.1.5")
      "request is not allowed" in {
        filter.isAllowed(headers) mustBe false
      }
    }

    "headers map does not contain a ip header" should  {
      val headers = Map.empty[String, String]
      "request is not allowed" in {
        filter.isAllowed(headers) mustBe false
      }
    }
  }
}
