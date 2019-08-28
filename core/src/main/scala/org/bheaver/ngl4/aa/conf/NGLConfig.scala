package org.bheaver.ngl4.aa.conf
import org.bheaver.ngl4.util.conf.DBSettings
import pureconfig.ConfigReader.Result
import pureconfig.generic.auto._

case class NGLConfig(dbSettings: DBSettings)