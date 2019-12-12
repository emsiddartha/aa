package org.bheaver.ngl4.aa.conf

import org.bheaver.ngl4.aa.authentication.{AuthenticationService, AuthenticationServiceNS}
import org.bheaver.ngl4.aa.authentication.datastore.{PatronDS, PatronDSNS}
import org.bheaver.ngl4.aa.protocol.authentication.{JWTService, JWTServiceNS}
import org.bheaver.ngl4.util.db.DBConnection
import pureconfig.ConfigReader.Result
import pureconfig.generic.auto._
class BeanRegistry {

}
object ConnectionToDB {
  val getNGLConfig:NGLConfig = {
    val value: Result[NGLConfig] = pureconfig.loadConfig[NGLConfig]
    val config = value.right.get
    config
  }
  val dbCon:DBConnection = DBConnection(getNGLConfig.dbSettings)

}
object AAServiceRegistry extends AuthenticationServiceNS with PatronDSNS with JWTServiceNS {
  override val authenticateService: AuthenticationService = new AuthenticationServiceImpl
  override val patronDS: PatronDS = new PatronDSImpl
  override val jwtService: JWTService = new JWTServiceImpl
}