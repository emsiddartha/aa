package org.bheaver.ngl4.aa.conf

import org.bheaver.ngl4.aa.authentication.{AuthenticationService, AuthenticationServiceImpl, JWTService, JWTServiceImpl}
import org.bheaver.ngl4.aa.authentication.datastore.{PatronDS, PatronDSImpl}
import org.bheaver.ngl4.util.db.DBConnection
import org.springframework.context.annotation.{Bean, Configuration, DependsOn}
import pureconfig.ConfigReader.Result
import pureconfig.generic.auto._

@Configuration
class BeanFactory {
  @Bean(Array("NGLConfig"))
  def getNGLConfig:NGLConfig = {
    val value: Result[NGLConfig] = pureconfig.loadConfig[NGLConfig]
    val config = value.right.get
    config
  }

  @Bean(Array("DBConnection"))
  def getDBConnection(nglConfig: NGLConfig):DBConnection = DBConnection(nglConfig.dbSettings)

  @Bean(Array("PatronDS"))
  def getPatronDS(dbConnection: DBConnection): PatronDS = new PatronDSImpl(dbConnection)

  @Bean(Array("JWTService"))
  def getJWTService: JWTService = new JWTServiceImpl

  @Bean(Array("AuthenticationService"))
  @DependsOn(Array("PatronDS", "JWTService"))
  def getAuthenticationService(patronDS: PatronDS, jwtService: JWTService): AuthenticationService = new AuthenticationServiceImpl(jwtService, patronDS)
}
