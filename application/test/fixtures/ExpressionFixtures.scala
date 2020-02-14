package fixtures

import play.api.libs.json.{JsObject, Json}

object ExpressionFixtures {
  private val orJsObject = Json.obj("or" -> Json.arr(
    Json.obj("matches" -> Json.obj("path" -> "$.a", "value" -> "a")),
    Json.obj("equal" -> Json.obj("path" -> "$.c", "value" -> true)),
  ))

  val nextedExpression: JsObject = Json.obj("and" -> Json.arr(
    orJsObject,
    Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8))
  ))

  val simpleEqualExpression: JsObject = Json.obj("equal" -> Json.obj("path" -> "$.foo", "value" -> "bar"))
}
