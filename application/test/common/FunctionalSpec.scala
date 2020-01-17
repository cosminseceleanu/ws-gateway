package common

import infrastructure.Environments
import org.scalatest._
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Injecting

abstract class FunctionalSpec extends FeatureSpec with MustMatchers with OptionValues
  with WsScalaTestClient with GuiceOneServerPerSuite with Injecting with GivenWhenThen with BeforeAndAfterAll {

  val httpClient: WSClient = app.injector.instanceOf[WSClient]
  val wsClient: AkkaWebSocketClient = new AkkaWebSocketClient

  val httpHost: String = s"http://localhost:$port"
  val wsHost: String = s"ws://localhost:$port"
  val api: String = s"http://localhost:$port/api/internal"

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder().configure(Map("gateway.env" -> Environments.TEST)).build()
  }

  def wsConnectionUrl(path: String, connectionId: String): String = s"$wsHost$path?${connectionIdQueryParam(connectionId)}"
  def connectionIdQueryParam(id: String): String = s"connectionId=$id"

}
