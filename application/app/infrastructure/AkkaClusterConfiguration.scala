package infrastructure

import akka.actor.ActorSystem
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import javax.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}
import play.api.Configuration


@Singleton
class AkkaClusterConfiguration @Inject()(system: ActorSystem, config: Configuration) {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val management = AkkaManagement(system).start()
  if (config.get[Boolean]("ws-gateway.akka.enable-cluster-bootstrap")) {
    ClusterBootstrap(system).start()
  }

  logger.info("Akka cluster configured")
}
