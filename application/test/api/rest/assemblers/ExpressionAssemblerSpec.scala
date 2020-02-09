package api.rest.assemblers

import common.UnitSpec
import common.rest.JsonSupport
import domain.exceptions.{ExpressionNotSupportedException, IncorrectBooleanExpressionException, IncorrectExpressionException}
import domain.model.Expression.{And, Equal, Matches, Or}
import fixtures.ExpressionFixtures
import play.api.libs.json.Json

class ExpressionAssemblerSpec extends UnitSpec with JsonSupport {

  private val assembler = new ExpressionAssembler

  "resource to model" when {
    "expression is just an equal" should {
      "JsObject is assembled as Equal Expression correctly" in {
        val resource = Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8))
        val expression = assembler.toModel(resource)

        expression.isInstanceOf[Equal[Number]] mustEqual true
      }
    }

    "root object has more than one element" should {
      "throw an exception" in {
        val resource = Json.obj(
          "equal" -> Json.obj("path" -> "$.b", "value" -> 8),
            "matches" -> Json.obj("path" -> "$.b", "value" -> "^b."),
          )

        an[IncorrectExpressionException] must be thrownBy assembler.toModel(resource)
      }
    }

    "value for equal expression does not contains a supported type" should {
      "throw an exception" in {
        val resource = Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> Json.obj("c" -> "b")))

        an[IncorrectExpressionException] must be thrownBy assembler.toModel(resource)
      }
    }

    "path is not a string" should {
      "throw an exception" in {
        val resource = Json.obj("equal" -> Json.obj("path" -> 10, "value" -> 8))

        an[IncorrectExpressionException] must be thrownBy assembler.toModel(resource)
      }
    }

    "name is not supported" should {
      "throw an exception" in {
        val resource = Json.obj("lteee" -> Json.obj("path" -> "$.b", "value" -> 8))

        an[ExpressionNotSupportedException] must be thrownBy assembler.toModel(resource)
      }
    }

    "boolean expression has 3 elements" should {
      "throw an exception" in {
        val resource = Json.obj("and" -> Json.arr(
          Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8)),
          Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8)),
          Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8))
          ))

        an[IncorrectBooleanExpressionException] must be thrownBy assembler.toModel(resource)
      }
    }

    "boolean expression child is not an json object" should {
      "throw an exception" in {
        val resource = Json.obj("and" -> Json.arr(
          Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8)),
          Json.arr("aa", "bb"),
          ))

        an[IncorrectBooleanExpressionException] must be thrownBy assembler.toModel(resource)
      }
    }

    "boolean expression is not a json array" should {
      "throw an exception" in {
        val resource = Json.obj("and" -> Json.obj("equal" -> Json.obj("path" -> "$.b", "value" -> 8)))

        an[IncorrectBooleanExpressionException] must be thrownBy assembler.toModel(resource)
      }
    }

    "boolean expression has more than 2 nested levels" should {
      "throw an exception" in {
        val equal = Json.obj("equal" -> Json.obj("path" -> "$.c", "value" -> true))
        val resource = Json.obj("and" -> Json.arr(
          Json.obj("or" -> Json.arr(
            Json.obj("or" -> Json.arr(equal, equal)),
            equal)),
          equal,
        ))
        val caught = intercept[IncorrectExpressionException] {
          assembler.toModel(resource)
        }

        caught.getMessage.contains("nested") mustBe true
      }
    }

    "expression has 2 nested levels" should {
      "JsObject is assembled as Expression" in {
        val resource = ExpressionFixtures.nextedExpression
        val expression = assembler.toModel(resource)

        expression.isInstanceOf[And] mustEqual true
        val root = expression.asInstanceOf[And]

        root.left.isInstanceOf[Or] mustEqual true
        root.right.isInstanceOf[Equal[Number]] mustEqual true

        val number = root.right.asInstanceOf[Equal[Number]]
        val or = root.left.asInstanceOf[Or]

        number.value mustEqual 8
        number.path mustEqual "$.b"

        or.left.isInstanceOf[Matches] mustEqual true
        or.right.isInstanceOf[Equal[Boolean]] mustEqual true
      }
    }
  }

  "model to resource" when {
    "simple expression" should {
      "Expression is assembled as JsObject" in {
        val expression = Equal("$.e", "b")
        val expected = Json.obj("equal" -> Json.obj("path" -> "$.e", "value" -> "b"))

        assembler.toResource(expression) mustEqual expected
      }
    }

    "terminal expression value is not supported" should {
      "throw an exception" in {
        val expression = Equal("$.e", Set("1"))

        an[IncorrectBooleanExpressionException] must be thrownBy assembler.toResource(expression)
      }
    }

    "nested expression" should {
      "Expression is assembled as JsObject" in {
        val expression = And(
          Or(Matches("$.a", "a"), Equal("$.c", true)),
          Equal("$.b", 8)
        )

        assembler.toResource(expression) mustEqual ExpressionFixtures.nextedExpression
      }
    }
  }
}
