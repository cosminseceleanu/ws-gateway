package infrastructure

import akka.actor.ActorSystem
import akka.management.scaladsl.AkkaManagement
import javax.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}


@Singleton
class AkkaClusterConfiguration @Inject()(actorSystem: ActorSystem) {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val management = AkkaManagement(actorSystem).start()

  logger.info("Akka cluster configured")
}
