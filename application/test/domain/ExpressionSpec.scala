package domain

import common.UnitSpec
import domain.model.{And, Equal, JsonPath, LiteralExpression, Matches, Or, StringExpression}

class ExpressionSpec extends UnitSpec {
  private val json = "{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95}"

  "debug expression" in {
    val expresion = Or(
      And(
        Matches(JsonPath("$.author", () => json), "^Nigel*"),
        Equal(StringExpression("cccc"), StringExpression("cccc")),
      ),
      Equal(JsonPath("$.category", () => json), StringExpression("reference"))
    )

    val result = expresion.evaluate()

    result mustBe true
  }
}
