package api.rest.resources

import common.{JsonResource, UnitSpec}

class FilterResourceSpec extends UnitSpec with JsonResource {
  "Empty FilterResource" when {
    "serialized as json" should {
      "all fields are present" in {
        val expectedJson = "{\"whitelistIps\":[],\"blacklistIps\":[],\"whitelistHosts\":[],\"blacklistHosts\":[]}"
        val resource = FilterResource()
        val json = toJson(resource)

        json mustEqual expectedJson
      }
    }
  }

  "FilterResource" when {
    "serialized as json" should {
      "filters values are present in json" in {
        val expectedJson = "{\"whitelistIps\":[\"1\"],\"blacklistIps\":[\"a\"],\"whitelistHosts\":[\"2\",\"3\"],\"blacklistHosts\":[]}"
        val resource = FilterResource(Set("1"), Set("a"), Set("2", "3"), Set.empty[String])
        val json = toJson(resource)

        json mustEqual expectedJson
      }
    }
  }

  "FilterResource" when {
    "deserialized from json" should {
      "resource has correct values" in {
        val initialJson = "{\"whitelistIps\":[\"1\"],\"blacklistIps\":[\"a\"],\"whitelistHosts\":[\"2\",\"3\"],\"blacklistHosts\":[]}"
        val resource = fromJson(initialJson)(FilterResource.format)

        resource.whitelistIps mustEqual(Set("1"))
        resource.blacklistIps mustEqual(Set("a"))
        resource.whitelistHosts mustEqual(Set("2", "3"))
        resource.blacklistHosts mustBe empty
      }
    }
  }
}
