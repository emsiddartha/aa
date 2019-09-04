package org.bheaver.ngl4.aa.authentication

import java.time.Clock
import java.util.Calendar

import com.typesafe.scalalogging.Logger
import pdi.jwt.{JwtAlgorithm, JwtJson4s}
import org.json4s._
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s.jackson.JsonMethods._
import pdi.jwt.{JwtAlgorithm, JwtJson4s}
import org.json4s._
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s.jackson.JsonMethods._

import scala.util.{Failure, Success}


case class EncodeRequest(patronId: String, libCode: String)

case class DecodeRequest(jwtToken: String, patronId: String, libCode: String, renewToken: Boolean = true, requestId: String = null)

trait JWTService {
  def encode(encodeRequest: EncodeRequest): String

  def isJWTValid(decodeRequest: DecodeRequest): Boolean

  def renewToken(decodeRequest: DecodeRequest): Option[String]

  def validateJWTToken(decodeRequest: DecodeRequest): String
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

      case Failure(exception) => false

      case Success(value) => {
        val value1 = parse(value._2.content).extract[Map[String, String]]
        value1("patronId").equals(decodeRequest.patronId) &&
          value1("libCode").equals(decodeRequest.libCode) &&
          value._2.issuer.getOrElse("").equals(iss) &&
          value._2.audience.map(audSet => audSet.contains(aud)).getOrElse(false) &&
          value._2.expiration.map(expriationTime => Calendar.getInstance().getTimeInMillis < expriationTime).getOrElse(false)
      }

    }
  }

  override def renewToken(decodeRequest: DecodeRequest): Option[String] = {
    implicit val clock = Clock.systemUTC()
    Option(decodeRequest)
      .map(decodeRequestIn => isJWTValid(decodeRequestIn))
      .map(isValid => if(isValid) {
        JwtJson4s.decodeAll(decodeRequest.jwtToken, secretKey, Seq(algo)) match {
          case Failure(exception) => null
          case Success(value) =>JwtJson4s.encode(value._2.expiresIn(30 * 60 ))
        }
      }else null)
  }

  override def validateJWTToken(decodeRequest: DecodeRequest): String = ???
}