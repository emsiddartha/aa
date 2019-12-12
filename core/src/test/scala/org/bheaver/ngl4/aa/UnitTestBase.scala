package org.bheaver.ngl4.aa

import org.scalamock.proxy.ProxyMockFactory
import org.scalamock.scalatest.{AsyncMockFactory, MockFactory}
import org.scalatest.{AsyncFlatSpec, FlatSpec, Inside, Inspectors, Matchers, OptionValues}

trait UnitTestBase  extends FlatSpec with Matchers with OptionValues with Inside with Inspectors with MockFactory

trait AsyncUnitTestBase  extends AsyncFlatSpec with Matchers with OptionValues with Inside with Inspectors with AsyncMockFactory
