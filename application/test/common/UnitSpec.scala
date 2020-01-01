package common

import org.scalamock.scalatest.MockFactory
import org.scalatest._

abstract class UnitSpec extends WordSpec with MustMatchers with OptionValues with Inside with Inspectors with MockFactory
