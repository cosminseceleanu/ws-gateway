package common

import org.scalatest._
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Injecting

abstract class FunctionalSpec extends FeatureSpec with MustMatchers with OptionValues with WsScalaTestClient with GuiceOneServerPerSuite with Injecting
