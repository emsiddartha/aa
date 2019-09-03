package org.bheaver.ngl4.aa.authentication

import org.bheaver.ngl4.util.UUIDGenerator
import com.typesafe.scalalogging.Logger
import org.bheaver.ngl4.aa.authentication.datastore.PatronDS
import org.bheaver.ngl4.aa.authentication.exceptions.{AuthenticationFailureException, BadRequestException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import org.bheaver.ngl4.util.DateUtil._

trait AuthenticationService {
  @throws(classOf[BadRequestException])
  @throws(classOf[AuthenticationFailureException])
  def authenticate(authenticationRequest: AuthenticationRequest): Future[AuthenticationSuccessResponse]
}

class AuthenticationServiceImpl(val jwtService: JWTService, val patronDS: PatronDS ) extends AuthenticationService {
  val logger = Logger(classOf[AuthenticationServiceImpl])
  override def authenticate(authenticationRequest: AuthenticationRequest): Future[AuthenticationSuccessResponse] = {
    if(authenticationRequest.libCode.isEmpty  || authenticationRequest.password.isEmpty || authenticationRequest.userName.isEmpty)
      throw new BadRequestException("Bad Request")
    patronDS.findByCredentials(authenticationRequest.userName,authenticationRequest.libCode, authenticationRequest.password)
      .map(mapOpt => {
        mapOpt match {
          case Some(value) => (value, new AuthenticationSuccessResponse("",
            value("patron_id"),
            value("fname"),
            value("mname"),
            value("lname"),
            "",
            value("dept_id"),
            "",
            value("patron_category_id"),
            UUIDGenerator.generateReturnRequestId(authenticationRequest.requestId)
          ))
          case None =>throw new AuthenticationFailureException("Authentication failed")
        }
      })
      .map(tuple => {
        val startDate = longStringToDateTime(tuple._1("membership_start_date"))
        val endDate = longStringToDateTime(tuple._1("membership_end_date"))
        if(endDate.isBeforeNow || startDate.isAfterNow) throw new AuthenticationFailureException("Membership dates")
        if(!tuple._1("status").equals("A")) throw new AuthenticationFailureException("Membership status failed")
        tuple._2
      })
      .map(authResponse => authResponse.copy(jwtToken = jwtService.encode(EncodeRequest(authResponse.patronId,authenticationRequest.libCode))))
  }
}