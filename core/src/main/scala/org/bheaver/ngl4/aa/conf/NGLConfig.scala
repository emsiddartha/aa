package org.bheaver.ngl4.aa.conf
case class DBSettings(mongoCluster: List[String])
case class NGLConfig(dbSettings: DBSettings)
import pureconfig.ConfigReader.Result
import pureconfig.generic.auto._

object NGLConfig{
  private val value: Result[NGLConfig] = pureconfig.loadConfig[NGLConfig]
  val config = value.right.get
}