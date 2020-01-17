package gateway.controllers

import akka.actor.ActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import gateway.events.GenericEvent
import javax.inject.Inject
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsValue
import play.api.mvc._

class ConnectionController @Inject()(
                                      val controllerComponents: ControllerComponents,
                                      val system: ActorSystem
                                 ) extends BaseController {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val mediator = DistributedPubSub(system).mediator

  def sendEvent(connectionId: String): Action[JsValue] = Action(parse.json) { implicit request =>
    val payload = request.body
    val topic = s"inbound.$connectionId"
    mediator ! Publish(topic, GenericEvent(connectionId, payload))

    NoContent
  }
}
