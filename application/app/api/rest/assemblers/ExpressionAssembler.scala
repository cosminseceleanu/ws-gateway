package api.rest.assemblers

import common.rest.ResourceAssembler
import domain.exceptions.{ExpressionNotSupportedException, IncorrectBooleanExpressionException, IncorrectExpressionException}
import domain.model.{BooleanExpression, Expression, TerminalExpression}
import domain.model.Expression._
import play.api.libs.json.{JsArray, JsBoolean, JsDefined, JsNumber, JsObject, JsString, JsValue, Json}

class ExpressionAssembler extends ResourceAssembler[Expression[Boolean], JsObject] {
  private val BOOLEAN_EXPRESSION_SIZE = 2
  private val MAX_NESTED_LEVEL = 2;

  override def toModel(resource: JsObject): Expression[Boolean] = {
    if (resource.value.size > 1) {
      throw new IncorrectExpressionException(s"Expression can have only one root")
    }
    parseJsValue(resource, 0)
  }

  private def parseJsValue(json: JsObject, nestedLevel: Int): Expression[Boolean] = {
    val (name, jsValue) = json.value.head

    if (nestedLevel > MAX_NESTED_LEVEL) {
      throw new IncorrectExpressionException(s"Expression can at most only ${MAX_NESTED_LEVEL} nested levels")
    }
    name match {
      case Expression.EQUAL => Equal(readFieldAsString(jsValue, "path"), readValue(jsValue))
      case Expression.MATCHES => Matches(readFieldAsString(jsValue, "path"), readFieldAsString(jsValue, "value"))
      case Expression.AND => createBooleanExpression(Expression.AND, jsValue, (left, right) => {
        And(parseJsValue(left, nestedLevel + 1), parseJsValue(right, nestedLevel + 1))
      })
      case Expression.OR => createBooleanExpression(Expression.OR, jsValue, (left, right) => {
        Or(parseJsValue(left, nestedLevel + 1), parseJsValue(right, nestedLevel + 1))
      })
      case _ => throw ExpressionNotSupportedException(name)
    }
  }

  private def readFieldAsString(jsValue: JsValue, field: String) = {
    (jsValue \ field) match {
      case JsDefined(JsString(value)) => value
      case _ => throw new IncorrectExpressionException(s"$field is not defined or is not a string")
    }
  }

  private def readValue(jsValue: JsValue) = {
    (jsValue \ "value") match {
      case JsDefined(JsString(value)) => value
      case JsDefined(JsNumber(value)) => value
      case JsDefined(JsBoolean(value)) => value
      case _ => throw new IncorrectExpressionException("Value is not defined or is not a supported type. Supported types are: string, number or boolean")
    }
  }

  private def createBooleanExpression(name: String, value: JsValue, createExpression: (JsObject, JsObject) => Expression[Boolean]): Expression[Boolean] = {
    value match {
      case JsArray(expressions) =>
        if (expressions.size != BOOLEAN_EXPRESSION_SIZE) {
          throw IncorrectBooleanExpressionException(s"Boolean expression $name must have exactly ${BOOLEAN_EXPRESSION_SIZE} child expressions")
        }
        val jsObjects = expressions.map({
          case obj: JsObject => obj
          case _ => throw IncorrectBooleanExpressionException(s"Boolean expressions must have exactly ${MAX_NESTED_LEVEL} child as json objects")
        })
        createExpression(jsObjects(0), jsObjects(1))
      case _ => throw IncorrectBooleanExpressionException("Boolean expressions body should be an array")
    }
  }

  override def toResource(model: Expression[Boolean]): JsObject = assembleJsObject(model, Json.obj())

  private def assembleJsObject(model: Expression[Boolean], jsObject: JsObject): JsObject = {
    val result = model match {
      case e: TerminalExpression[Boolean, _] =>
        e.value match {
          case v: String => jsObject + (e.name, Json.obj("path" -> e.path, "value" -> v))
          case v: BigDecimal => jsObject + (e.name, Json.obj("path" -> e.path, "value" -> v))
          case v: Int => jsObject + (e.name, Json.obj("path" -> e.path, "value" -> v))
          case v: Boolean => jsObject + (e.name, Json.obj("path" -> e.path, "value" -> v))
          case _ => throw IncorrectBooleanExpressionException(s"Expression value of type is not supported")
        }
      case e: BooleanExpression => assembleBooleanExpressionJsArray(jsObject, e)
    }

    result
  }

  private def assembleBooleanExpressionJsArray(jsObject: JsObject, e: BooleanExpression) = {
    jsObject + (e.name, Json.arr(
      assembleJsObject(e.left, Json.obj()),
      assembleJsObject(e.right, Json.obj())
    ))
  }
}
