package org.bheaver.ngl4.aa.authentication


import com.typesafe.scalalogging.Logger
import org.bheaver.ngl4.aa.{AsyncUnitTestBase, UnitTestBase}
import org.bheaver.ngl4.aa.authentication.datastore.PatronDS
import org.bheaver.ngl4.aa.authentication.exceptions.AuthenticationFailureException
import org.bheaver.ngl4.aa.protocol.authentication.{EncodeRequest, JWTService}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.AsyncFlatSpec
import org.scalatest.RecoverMethods._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.global
import org.bheaver.ngl4.util.DateUtil._
import org.bheaver.ngl4.util.exceptions.BadRequestException

import scala.annotation.elidable

class AuthenticationSync extends UnitTestBase {
  val logger = Logger(classOf[AuthenticationSync])
  behavior of "Authentication Request"
  it should "throw BadRequest if any parameter except request id is empty" in {
    val s: PatronDS = mock[PatronDS]
    val service: JWTService = mock[JWTService]
    val authenticationServiceImpl: AuthenticationService = new AuthenticationServiceImpl(service, s)

    assertThrows[BadRequestException] {
      authenticationServiceImpl.authenticate(AuthenticationRequest("", "", "", ""))
    }
  }
}

class Authentication extends AsyncUnitTestBase {
  val logger = Logger(classOf[Authentication])
  implicit val exeuctor = global
  behavior of "Authentication Request"
  it should "not throw BadRequest even if the request id is empty" in {
    val patronDSMock: PatronDS = mock[PatronDS]
    val jwtMock: JWTService = mock[JWTService]
    (patronDSMock.findByCredentials _).expects("1", "lib1", "abc") returns Future {
      Option(
        Map("patron_id" -> "1"
          , "fname" -> "Sudhakar"
          , "mname" -> "Sudhakar"
          , "lname" -> "Sudhakar"
          , "dept_id" -> "1"
          , "patron_category_id" -> "1"
          , "membership_start_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, -10)))
          , "membership_end_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, 5)))
          , "status" -> "A")
      )
    }
    (jwtMock.encode _) expects EncodeRequest("1", "lib1") returns ("JWT Token")
    val authService: AuthenticationService = new AuthenticationServiceImpl(jwtMock, patronDSMock)


    val authresponseFut = authService.authenticate(AuthenticationRequest("lib1", "1", "abc", null))
    authresponseFut.map((authRes) => {
      assert(authRes != null)
    }
    )
  }
  it should "throw Authentication Exception when Membership is expired" in {
    val patronDSMock: PatronDS = mock[PatronDS]
    val jwtMock: JWTService = mock[JWTService]
    (patronDSMock.findByCredentials _).expects("1", "lib1", "abc") returns Future {
      Option(
        Map("patron_id" -> "1"
          , "membership_start_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, -10)))
          , "membership_end_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, -1)))
        ).withDefault(str => "None")
      )
    }
    //(jwtMock.encode _) expects EncodeRequest("1", "lib1") returns  ("JWT Token")
    val authService: AuthenticationService = new AuthenticationServiceImpl(jwtMock, patronDSMock)


    val authresponseFut = authService.authenticate(AuthenticationRequest("lib1", "1", "abc", null))
    recoverToSucceededIf[AuthenticationFailureException](authresponseFut)
  }
  it should "throw Authentication Exception when Membership is on hold" in {
    val patronDSMock: PatronDS = mock[PatronDS]
    val jwtMock: JWTService = mock[JWTService]
    (patronDSMock.findByCredentials _).expects("1", "lib1", "abc") returns Future {
      Option(
        Map("patron_id" -> "1"
          , "status" -> "B"
          , "membership_start_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, -10)))
          , "membership_end_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, 5))))
          .withDefault(str => "None")
      )
    }
    //(jwtMock.encode _) expects EncodeRequest("1", "lib1") returns  ("JWT Token")
    val authService: AuthenticationService = new AuthenticationServiceImpl(jwtMock, patronDSMock)


    val authresponseFut = authService.authenticate(AuthenticationRequest("lib1", "1", "abc", null))
    recoverToSucceededIf[AuthenticationFailureException](authresponseFut)

  }
  it should "return valid response with valid data" in {
    val patronDSMock: PatronDS = mock[PatronDS]
    val jwtMock: JWTService = mock[JWTService]
    (patronDSMock.findByCredentials _).expects("DTE6DAY1", "lib1", "abc") returns Future {
      Option(
        Map("patron_id" -> "DTE6DAY1"
          , "fname" -> "Sudhakar"
          , "dept_id" -> "1"
          , "patron_category_id" -> "1"
          , "membership_start_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, -10)))
          , "membership_end_date" -> String.valueOf(dateTimeToLong(addDays(currentDate, 5)))
          , "status" -> "A")
          .withDefault(str => "None")
      )
    }
    (jwtMock.encode _) expects EncodeRequest("DTE6DAY1", "lib1") returns ("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCaGVhdmVyIEluYyIsImF1ZCI6Ik5HTDQgQXBwIiwiaWF0IjoxNTY3NDIxODA4OTc0LCJleHAiOjE1Njc0MjM2MDg5NzQsInBhdHJvbklkIjoiRFRFNkRBWTEiLCJsaWJDb2RlIjoibGliMSJ9.GWfqkTkcWMVMjAUPoIgMJ7EpaHkuXPJ-TEgJ4HMX_iU")
    val authService: AuthenticationService = new AuthenticationServiceImpl(jwtMock, patronDSMock)
    val authresponseFut = authService.authenticate(AuthenticationRequest("lib1", "DTE6DAY1", "abc", null))
    authresponseFut.map(authRes => {
      assert(List(
        assert(authRes.jwtToken == "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCaGVhdmVyIEluYyIsImF1ZCI6Ik5HTDQgQXBwIiwiaWF0IjoxNTY3NDIxODA4OTc0LCJleHAiOjE1Njc0MjM2MDg5NzQsInBhdHJvbklkIjoiRFRFNkRBWTEiLCJsaWJDb2RlIjoibGliMSJ9.GWfqkTkcWMVMjAUPoIgMJ7EpaHkuXPJ-TEgJ4HMX_iU"),
        assert(authRes.patronId == "DTE6DAY1"),
        assert(authRes.fname == "Sudhakar"),
        assert(authRes.departmentId == "1"),
        assert(authRes.patronCategoryId == "1"),
        assert(authRes.requestId.nonEmpty)
      ).filter(assertion => assertion == succeed).size == 6)
    })
  }

}
