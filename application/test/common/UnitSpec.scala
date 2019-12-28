package common

import org.scalatest._

abstract class UnitSpec extends WordSpec with MustMatchers with OptionValues with Inside with Inspectors
