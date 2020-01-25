package org.bheaver.ngl4.aa.conf

import com.google.inject.{AbstractModule, Provides}
import org.bheaver.ngl4.aa.authentication.{AuthenticationService, AuthenticationServiceImpl}
import org.bheaver.ngl4.aa.authentication.datastore.{PatronDS, PatronDSImpl}
import org.bheaver.ngl4.aa.protocol.authentication.{JWTService, JWTServiceImpl}
import org.bheaver.ngl4.util.db.DBConnection
import pureconfig.ConfigReader.Result
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._

class ProdModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[DBConnection]).toProvider(classOf[ConnectionToDB])
    bind(classOf[PatronDS]).to(classOf[PatronDSImpl])
    bind(classOf[JWTService]).to(classOf[JWTServiceImpl])
    bind(classOf[AuthenticationService]).to(classOf[AuthenticationServiceImpl])
  }

  @Provides
  def provideNGLConfig: Result[NGLConfig] = pureconfig.loadConfig[NGLConfig]
}