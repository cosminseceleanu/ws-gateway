package gateway.flow.dto

import domain.model.{Backend, BackendSettings}
import gateway.events.InboundEvent

case class BackendResult(event: InboundEvent, backend: Backend[BackendSettings], result: Either[Exception, Unit])
