package gateway.events

import common.Sha
import play.api.libs.json.{JsValue, Json}

sealed trait OutboundEvent extends Event

case class GenericEvent(connectionId: String, payload: JsValue) extends OutboundEvent

case class AckEvent(connectionId: String, originalMsg: JsValue) extends OutboundEvent {
  override val payload: JsValue = Json.obj(
    "connectionId" -> connectionId,
    "payloadHash" -> Sha.hash(originalMsg.toString()),
    "ack" -> true
  )
}