package org.bheaver.ngl4.aa.authentication

import com.mongodb.client.model.Filters
import com.typesafe.scalalogging.Logger
import org.bheaver.ngl4.aa.authentication.datastore.{PatronDS, PatronDSImpl}
import org.bheaver.ngl4.aa.authentication.exceptions.{AuthenticationFailureException, BadRequestException}
import org.bheaver.ngl4.aa.db.DBConnection
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.collection.immutable.Document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

trait AuthenticationService {
  @throws(classOf[BadRequestException])
  @throws(classOf[AuthenticationFailureException])
  def authenticate(authenticationRequest: AuthenticationRequest): Future[AuthenticationSuccessResponse]
}

class AuthenticationServiceImpl extends AuthenticationService {
  val logger = Logger(classOf[AuthenticationServiceImpl])
  val jwtService: JWTService = new JWTServiceImpl
  override def authenticate(authenticationRequest: AuthenticationRequest): Future[AuthenticationSuccessResponse] = {
    implicit val mongoDatabase:MongoDatabase = DBConnection(authenticationRequest.libCode)
    val patronDS: PatronDS = new PatronDSImpl
    patronDS.findByCredentials(authenticationRequest.userName,authenticationRequest.password)
      .map(documentOpt => {
        documentOpt match {
          case Some(value) =>new AuthenticationSuccessResponse("",value.getString("patron_id"),value.getString("fname"),value.getString("mname"),value.getString("lname"),"dept","patcat")
          case None =>throw new AuthenticationFailureException("Authentication failed")
        }
      }).map(authResponse => authResponse.copy(jwtToken = jwtService.encode(EncodeRequest(authResponse.patronId,authenticationRequest.libCode))))
  }
}