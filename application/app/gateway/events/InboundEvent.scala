package gateway.events

import play.api.libs.json.JsValue

sealed trait InboundEvent extends Event {
  val payload: JsValue
}

case class DefaultInboundEvent(connectionId: String, payload: JsValue) extends InboundEvent
case class AckEvent(connectionId: String, payload: JsValue) extends InboundEvent