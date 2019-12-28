package api.controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.ws.WSClient
import play.api.test._
import play.api.test.Helpers._

class HomeControllerSpec extends PlaySpec with GuiceOneServerPerSuite with Injecting {

  "HomeController GET" should {
    "return hello world" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val endpoint = s"http://localhost:$port"
      // await is from play.api.test.FutureAwaits
      val response = await(wsClient.url(endpoint).get())

      response.status mustBe OK
      response.body mustBe "Hello World"
    }
  }
}
