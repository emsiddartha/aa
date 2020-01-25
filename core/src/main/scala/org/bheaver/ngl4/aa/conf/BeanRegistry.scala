package org.bheaver.ngl4.aa.conf

import com.google.inject.{Guice, Inject, Provider, Singleton}
import org.bheaver.ngl4.aa.authentication.AuthenticationService
import org.bheaver.ngl4.aa.protocol.authentication.JWTService
import org.bheaver.ngl4.util.db.DBConnection
import pureconfig.ConfigReader.Result
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._

object BeanRegistry {
  val injector  = Guice.createInjector(new ProdModule)
  def getAuthenticationService: AuthenticationService = injector.getInstance(classOf[AuthenticationService])
  def getJWTService: JWTService = injector.getInstance(classOf[JWTService])
}

@Singleton
class ConnectionToDB @Inject() (resultNGLConf: Result[NGLConfig]) extends Provider[DBConnection]{
  val getNGLConfig:NGLConfig = {
    val config = resultNGLConf.right.get
    config
  }

  override def get(): DBConnection = DBConnection(getNGLConfig.dbSettings)
}