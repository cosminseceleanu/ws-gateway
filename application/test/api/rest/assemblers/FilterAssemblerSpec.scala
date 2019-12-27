package api.rest.assemblers

import api.rest.resources.FilterResource
import common.UnitSpec
import domain.model.Filter

class FilterAssemblerSpec extends UnitSpec {
  private val filterAssembler  = new FilterAssembler
  private val blacklistIps: Set[String] = Set("127.0.1.1")
  private val whitelistIps = Set("127.0.0.1")
  private val blacklistHosts = Set("localhost")
  private val whitelistHosts = Set("example.com")


  "Filters to FilterResource" when {
    "filters are empty" should {
      "resource has no filters" in {
        val resource = filterAssembler.toResource(Set.empty[Filter])

        resource.blacklistHosts mustBe empty
        resource.whitelistHosts mustBe empty
        resource.whitelistIps mustBe empty
        resource.blacklistIps mustBe empty
      }
    }

    "all filters given" should {
      "resource has right filter values" in {
        val resource = filterAssembler.toResource(Set(
          Filter.blacklistIps(blacklistIps),
          Filter.whitelistIps(whitelistIps),
          Filter.blacklistHosts(blacklistHosts),
          Filter.whitelistHosts(whitelistHosts)
          ))

        resource.blacklistIps mustBe blacklistIps
        resource.whitelistIps mustBe whitelistIps
        resource.blacklistHosts mustBe blacklistHosts
        resource.whitelistHosts mustBe whitelistHosts
      }
    }
  }

  "FilterResource to Filters" when {
    "resource has all values empty" should  {
      "result is an empty set" in {
        val result = filterAssembler.toModel(FilterResource())

        result mustBe empty
      }
    }

    "resource has only whitelist ips" should {
      "result contains right filter" in {
        val resource = FilterResource().copy(whitelistIps = whitelistIps)
        val result = filterAssembler.toModel(resource)

        result must have size(1)
        result must contain(Filter.whitelistIps(whitelistIps))
      }
    }

    "resource has ips and hosts" should {
      "result contains ips and hosts filters" in {
        val result = filterAssembler.toModel(FilterResource(whitelistIps, blacklistIps, whitelistHosts,blacklistHosts))

        result must have size(4)
        result must contain(Filter.whitelistIps(whitelistIps))
        result must contain(Filter.blacklistIps(blacklistIps))
        result must contain(Filter.whitelistHosts(whitelistHosts))
        result must contain(Filter.blacklistHosts(blacklistHosts))
      }
    }
  }
}
