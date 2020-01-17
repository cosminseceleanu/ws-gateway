package functional.gateway

import java.util.concurrent.TimeUnit

import common.AkkaWebSocketClient
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers._
import play.api.test._

class WsFrontControllerIT extends PlaySpec with GuiceOneServerPerSuite with Injecting {
  val wsClient = new AkkaWebSocketClient

  "WsFrontController" should {
    "for a sent message an ack is received" in {
      val serverUrl = s"ws://localhost:$port/ws"
      val (in, out) = await(wsClient.connect(serverUrl))
      await(out.offer("Hello"), 1000, TimeUnit.MILLISECONDS)

      in.poll(1000, TimeUnit.MILLISECONDS) mustBe "Hello ack"

    }
  }
}
