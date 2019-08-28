package org.bheaver.ngl4.aa.authentication

import com.typesafe.scalalogging.Logger
import org.bheaver.ngl4.aa.UnitTestBase
import org.bheaver.ngl4.aa.authentication.exceptions.BadRequestException

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.Matchers._

class Authentication extends UnitTestBase {
  val logger = Logger(classOf[Authentication])
  def fixture = new {
    //val authenticationServiceImpl = new AuthenticationServiceImpl
    val authenticationServiceImpl:AuthenticationService = null
  }

  "Authentication Request" should "throw BadRequest if any parameter except request id is empty" in {
    val f = fixture
    assertThrows[BadRequestException] {
      val authresponseFut = f.authenticationServiceImpl.authenticate(AuthenticationRequest("", "", "", Option("")))
      authresponseFut.onComplete((authresponse) => authresponse.get)
    }
  }
  "Authentication Request" should "not throw BadRequest even if the request id is empty" in {
    val f = fixture
    val authresponseFut = f.authenticationServiceImpl.authenticate(AuthenticationRequest("someLibCode","someUserName","somePassword",null))
    authresponseFut.map((authRes)=> {
      authRes should not equal(null)
    })
  }

}
