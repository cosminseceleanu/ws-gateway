package gateway.connection

import akka.actor.ActorRef
import domain.model.Endpoint

case class ConnectionContext(connectionId: String, out: ActorRef, endpoint: Endpoint)
