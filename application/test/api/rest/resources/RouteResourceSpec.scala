package api.rest.resources

import common.UnitSpec
import common.rest.JsonSupport
import fixtures.BackendFixtures
import org.scalatest.Matchers._

class RouteResourceSpec extends UnitSpec with JsonSupport {

  private val fullJson = "{\"type\":\"connect\",\"name\":\"name\",\"http\":[{\"destination\":\"http://localhost\"," +
    "\"additionalHeaders\":{\"X-Debug\":\"foo\"},\"timeout\":1}],\"kafka\":[{\"topic\":\"some.topic\"}]}"

  private val customRouteJson = "{\"type\":\"custom\",\"name\":\"Custom route\",\"expression\":{\"and\":[{\"or\":[{\"matches\":{\"path\":\"$.a\",\"value\":\"a\"}}," +
                                "{\"gte\":{\"path\":\"$.b\",\"value\":8}}]},{\"equal\":{\"path\":\"$.b\",\"value\":\"b\"}}]}," +
                                "\"http\":[{\"destination\":\"service.1.example.com/custom\"}]}"

  private val httpBackend = BackendFixtures.httpBackendResource
  private val kafkaBackend = BackendFixtures.kafkaBackendResource

  "RouteResource with only http backends" when {
    "serialized as json" should {
      "should have all fields serialized correctly" in {
        val expectedJson = "{\"type\":\"connect\",\"name\":\"name\",\"http\":[{\"destination\":\"http://localhost\"," +
          "\"additionalHeaders\":{\"X-Debug\":\"foo\"},\"timeout\":1}],\"kafka\":[]}"
        val resource = RouteResource("connect", "name", Set(httpBackend), Set.empty)
        val json = toJson(resource)

        json mustEqual expectedJson
      }
    }
  }

  "RouteResource with all backends" when {
    "serialized as json" should {
      "should have all backends serialized correctly" in {
        val resource = RouteResource("connect", "name", Set(httpBackend), Set(kafkaBackend))
        val json = toJson(resource)

        json mustEqual fullJson
      }
    }
  }

  "RouteResource json with all backends" when {
    "deserialized from json" should {
      "should have all backends deserialized correctly" in {
        val result = fromJson[RouteResource](fullJson)

        result.name mustEqual "name"
        result.routeType mustEqual "connect"
        result.expression.isEmpty mustEqual true
        result.http should contain(httpBackend)
        result.kafka should contain(kafkaBackend)
      }
    }
  }

  "RouteResource json without backends" when {
    "deserialized from json" should {
      "should have present fields deserialized correctly" in {
        val result = fromJson[RouteResource]("{\"type\":\"connect\",\"name\":\"name\"}")

        result.name mustEqual "name"
        result.routeType mustEqual "connect"
        result.http shouldBe empty
        result.kafka shouldBe empty
      }
    }
  }

  "RouteResource json with expression" when {
    "deserialized from json" should {
      "expression should be a json object" in {
        val result = fromJson[RouteResource](customRouteJson)

        result.routeType mustEqual "custom"
        result.expression.isEmpty mustEqual false
      }
    }
  }

}
