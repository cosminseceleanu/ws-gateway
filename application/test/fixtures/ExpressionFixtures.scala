package fixtures

import play.api.libs.json.Json

object ExpressionFixtures {
  private val orJsObject = Json.obj("or" -> Json.arr(
    Json.obj("matches" -> Json.obj("path" -> "$.a", "value" -> "a")),
    Json.obj("equal" -> Json.obj("path" -> "$.c", "value" -> true)),
  ))

  val nextedExpression = Json.obj("and" -> Json.arr(
    orJsObject,
    Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8))
  ))
}
