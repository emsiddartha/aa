package org.bheaver.ngl4.aa.protocol.authentication

import java.time.Clock
import java.util.Calendar

import com.typesafe.scalalogging.Logger
import org.bheaver.ngl4.aa.protocol.exceptions.{ExpiredTokenException, InvalidTokenException}
import org.bheaver.ngl4.aa.protocol.model.{DecodeRequest, JWTRenewTokenResponse}
import org.bheaver.ngl4.util.StringUtil._
import org.bheaver.ngl4.util.UUIDGenerator
import org.bheaver.ngl4.util.exceptions.BadRequestException
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import pdi.jwt.{JwtAlgorithm, JwtJson4s}

import scala.util.{Failure, Success}


case class EncodeRequest(patronId: String, libCode: String)


trait JWTService {
  def encode(encodeRequest: EncodeRequest): String

  def isJWTValid(decodeRequest: DecodeRequest): Boolean

  def renewToken(decodeRequest: DecodeRequest): JWTRenewTokenResponse

}

class JWTServiceImpl extends JWTService {
  implicit val formats = DefaultFormats
  val logger = Logger(classOf[JWTServiceImpl])
  val iss = "Bheaver Inc"
  val aud = "NGL4 App"
  val secretKey = "somethingWeDontKnow"
  val algo = JwtAlgorithm.HS256

  override def encode(encodeRequest: EncodeRequest): String = {
    val iat = Calendar.getInstance().getTimeInMillis
    val exp = iat + (30 * 60 * 1000)
    val claim = JObject(("iss", iss), ("aud", aud), ("iat", iat), ("exp", exp), ("patronId", encodeRequest.patronId), ("libCode", encodeRequest.libCode))
    JwtJson4s.encode(claim, secretKey, algo)
  }

  override def isJWTValid(decodeRequest: DecodeRequest): Boolean = {
    JwtJson4s.decodeAll(decodeRequest.jwtToken, secretKey, Seq(algo)) match {

      case Failure(exception) => {
        logger.error(exception.getMessage)
        throw new InvalidTokenException("Invalid token")
        false
      }

      case Success(value) => {
        value._2.expiration match {
          case Some(value) => if(value<System.currentTimeMillis()) throw new ExpiredTokenException("Token expired")
          case None => throw new InvalidTokenException("Invalid token")
        }
        val value1 = parse(value._2.content).extract[Map[String, String]]
        value1("patronId").equals(decodeRequest.patronId) &&
          value1("libCode").equals(decodeRequest.libCode) &&
          value._2.issuer.getOrElse("").equals(iss) &&
          value._2.audience.map(audSet => audSet.contains(aud)).getOrElse(false) &&
          value._2.expiration.map(expriationTime => Calendar.getInstance().getTimeInMillis < expriationTime).getOrElse(false)
      }

    }
  }

  override def renewToken(decodeRequest: DecodeRequest): JWTRenewTokenResponse = {

    implicit val clock = Clock.systemUTC()
    if(isEmpty(decodeRequest.patronId) || isEmpty(decodeRequest.libCode) || isEmpty(decodeRequest.jwtToken)) throw new BadRequestException("Not enough parameters")
    Option(decodeRequest)
      .map(decodeRequestIn => isJWTValid(decodeRequestIn))
      .map(isValid => if(isValid) {
        JwtJson4s.decodeAll(decodeRequest.jwtToken, secretKey, Seq(algo)) match {
          case Failure(exception) => null
          case Success(value) => if(decodeRequest.renewToken) encode(EncodeRequest(decodeRequest.patronId,decodeRequest.libCode)) else decodeRequest.jwtToken
        }
      }else null)
      .map(token => JWTRenewTokenResponse(token,UUIDGenerator.generateReturnRequestId(decodeRequest.requestId)))
      .get
  }
}