package common

import org.scalatest._

abstract class UnitSpec extends FlatSpec with MustMatchers with OptionValues with Inside with Inspectors
