package common

import org.scalatest._
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Injecting

abstract class FunctionalSpec extends FeatureSpec with MustMatchers with OptionValues
  with WsScalaTestClient with GuiceOneServerPerSuite with Injecting with GivenWhenThen {

  val wsClient: WSClient = app.injector.instanceOf[WSClient]
  val host: String = s"http://localhost:$port"
  val api: String = s"http://localhost:$port/api/internal"
  val contentTypeHeader: (String, String) = ("Content-Type", "application/json")
}
