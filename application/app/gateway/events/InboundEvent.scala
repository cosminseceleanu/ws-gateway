package gateway.events

import play.api.libs.json.{JsValue, Json}

sealed trait InboundEvent extends Event

case class Connected(connectionId: String) extends InboundEvent {
  override val payload: JsValue = Json.obj("connectionId" -> connectionId)
}

case class Disconnected(connectionId: String) extends InboundEvent {
  override val payload: JsValue = Json.obj("connectionId" -> connectionId)
}

case class ReceivedEvent(connectionId: String, payload: JsValue) extends InboundEvent
