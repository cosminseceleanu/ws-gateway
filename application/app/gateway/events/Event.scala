package gateway.events

import play.api.libs.json.JsValue

trait Event {
  val connectionId: String
  val payload: JsValue
}
