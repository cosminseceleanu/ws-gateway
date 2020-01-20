package gateway.connection

import java.util.UUID

import infrastructure.Environments
import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class IdGenerator @Inject() (configuration: Configuration) {
  def generate(source: Option[String]): String = source match {
    case None => generateRandomUuid
    case Some(value) =>
      if (Environments.TEST == configuration.get[String]("gateway.env")) {
        value
      } else {
        generateRandomUuid
      }
  }

  private def generateRandomUuid = {
    UUID.randomUUID().toString
  }
}
