package domain

import common.UnitSpec
import domain.model.Expression._

class ExpressionSpec extends UnitSpec {
  private val json = "{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95}"

  "debug expression" in {
    val expresion = Or(
      And(
        Matches("$.author", "^Nigel*"),
        Equal("cccc", "cccc"),
      ),
      Equal("$.price", 8.95)
    )

    val result = expresion.evaluate(json)

    result mustBe true
  }
}
