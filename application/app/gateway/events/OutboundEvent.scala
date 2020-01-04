package gateway.events

import play.api.libs.json.JsValue

sealed trait OutboundEvent extends Event

case class Connected(connectionId: String) extends OutboundEvent
case class Disconnected(connectionId: String) extends OutboundEvent
case class ReceivedEvent(connectionId: String, payload: JsValue) extends OutboundEvent
