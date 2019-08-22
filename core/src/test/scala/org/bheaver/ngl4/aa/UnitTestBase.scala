package org.bheaver.ngl4.aa

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Inside, Inspectors, Matchers, OptionValues}

abstract class UnitTestBase  extends FlatSpec with Matchers with OptionValues with Inside with Inspectors with MockFactory
