package org.bheaver.ngl4.aa.conf

import org.bheaver.ngl4.aa.authentication.{AuthenticationService, AuthenticationServiceImpl, JWTService, JWTServiceImpl}
import org.bheaver.ngl4.aa.authentication.datastore.{PatronDS, PatronDSImpl}
import org.springframework.context.annotation.{Bean, Configuration, DependsOn}

@Configuration
class BeanFactory {
  @Bean(Array("PatronDS"))
  def getPatronDS: PatronDS = new PatronDSImpl

  @Bean(Array("JWTService"))
  def getJWTService: JWTService = new JWTServiceImpl

  @Bean(Array("AuthenticationService"))
  @DependsOn(Array("PatronDS", "JWTService"))
  def getAuthenticationService(patronDS: PatronDS, jwtService: JWTService): AuthenticationService = new AuthenticationServiceImpl(jwtService, patronDS)
}
