package org.bheaver.ngl4.aa.authentication

import com.typesafe.scalalogging.Logger
import org.bheaver.ngl4.aa.UnitTestBase
import org.bheaver.ngl4.aa.authentication.exceptions.{BadRequestException, ExpiredTokenException, InvalidTokenException}

class JWT extends UnitTestBase {
  val logger = Logger(classOf[JWT])

  def fixture = new {
    val jwtService = new JWTServiceImpl
  }
  behavior of "JWTService"


  it should "throw BadRequestException if DecodeRequest does not have userId or libCode or JWTToken" in {
    val f = fixture
    assertThrows[BadRequestException](f.jwtService.renewToken(DecodeRequest("", "", "")))
  }

  it should "throw ExpiredTokenException if DecodeRequest has expired token" in{
    val f = fixture
    assertThrows[ExpiredTokenException](f.jwtService.renewToken(DecodeRequest("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCaGVhdmVyIEluYyIsImF1ZCI6Ik5HTDQgQXBwIiwiaWF0IjoxNTY3NTU3MzA3NTc2LCJleHAiOjE1Njc1NTkxMDc1NzYsInBhdHJvbklkIjoiRFRFNkRBWTEiLCJsaWJDb2RlIjoibGliMSJ9.9Jm7oUslrMUJ64sUdVEL05rKyP-ixb1CFpANX7ohK1U","DTE6DAY1","lib1")))
  }
  it should "throw InvalidTokenException if JWTToken is invalid" in{
    val f = fixture
    assertThrows[InvalidTokenException](f.jwtService.renewToken(DecodeRequest("SomeJunkToken","DTE6DAY1","lib1")))
  }
  it should "throw InvalidTokenException if DecodeRequest has token claims that do not match with userId and libCode" in{
    val f = fixture
    assertThrows[ExpiredTokenException](f.jwtService.renewToken(DecodeRequest("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCaGVhdmVyIEluYyIsImF1ZCI6Ik5HTDQgQXBwIiwiaWF0IjoxNTY3NTU3MzA3NTc2LCJleHAiOjE1Njc1NTkxMDc1NzYsInBhdHJvbklkIjoiRFRFNkRBWTEiLCJsaWJDb2RlIjoibGliMSJ9.9Jm7oUslrMUJ64sUdVEL05rKyP-ixb1CFpANX7ohK1U","1","lib1")))
  }

  it should "generate a new valid token if everything is valid and renewToken is true" in{

  }

  it should "return same token if everything is valid and renewToken is false" in{

  }
}
