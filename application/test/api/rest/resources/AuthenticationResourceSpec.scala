package api.rest.resources

import common.UnitSpec
import common.rest.JsonSupport

class AuthenticationResourceSpec extends UnitSpec with JsonSupport {
  "serialize to jsom" when {
    "None Auth" should {
      "all fields are present" in {
        val expectedJson = "{\"mode\":\"none\"}"
        val json = toJson(AuthenticationResource.none())

        json mustEqual expectedJson
      }
    }

    "Basic Auth" should {
      "all fields are present" in {
        val expectedJson = "{\"mode\":\"basic\",\"username\":\"user\",\"password\":\"pass\"}"
        val json = toJson(AuthenticationResource.basic("user", "pass"))

        json mustEqual expectedJson
      }
    }

    "Bearer Auth" should {
      "all fields are present" in {
        val expectedJson = "{\"mode\":\"bearer\",\"verifyTokenUrl\":\"http://localhost:8080/verify_token\"}"
        val json = toJson(AuthenticationResource.bearer("http://localhost:8080/verify_token"))

        json mustEqual expectedJson
      }
    }
  }

  "deserialize from json" when {
    "Basic Auth" should {
      "resource has right values " in {
        val json = "{\"mode\":\"basic\",\"username\":\"user\",\"password\":\"pass\"}"
        val resource = fromJson[AuthenticationResource](json)

        resource.mode mustEqual AuthenticationResource.MODE_BASIC
        resource.username mustEqual Some("user")
        resource.password mustEqual Some("pass")
      }
    }
  }
}
