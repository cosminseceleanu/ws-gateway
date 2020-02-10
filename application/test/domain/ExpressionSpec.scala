package domain

import common.UnitSpec
import domain.model.Expression._

class ExpressionSpec extends UnitSpec {

  "Evaluate expression" when {
    "expression is nested" should {
      "match the input json" in {
        val expresion = Or(
          And(
            Matches("$.author", "^Nigel*"),
            Equal("cccc", "cccc"),
          ),
          Equal("$.price", 8.95)
        )
        val json = "{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95}"

        expresion.evaluate(json) mustBe true
      }
    }

    "expression is equal and json path selects from array" should {
      "match the input json" in {
        val json = "{\"phoneNumbers\":[{\"type\":\"iPhone\",\"number\":\"0123-4567-8888\"},{\"type\":\"home\",\"number\":\"0123-4567-8910\"}]}"

        val expresion = Equal("$.phoneNumbers[0].number", "0123-4567-8888")

        expresion.evaluate(json) mustBe true
      }
    }
  }
}
