package org.bheaver.ngl4.aa.authentication

import com.mongodb.client.model.Filters
import org.bheaver.ngl4.aa.authentication.exceptions.{AuthenticationFailureException, BadRequestException}
import org.bheaver.ngl4.aa.db.DBConnection
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.collection.immutable.Document
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

trait AuthenticationService {
  @throws(classOf[BadRequestException])
  @throws(classOf[AuthenticationFailureException])
  def authenticate(authenticationRequest: AuthenticationRequest): Future[AuthenticationSuccessResponse]
}

class AuthenticationServiceImpl extends AuthenticationService {
  override def authenticate(authenticationRequest: AuthenticationRequest): Future[AuthenticationSuccessResponse] = {
    val db = DBConnection.database
    val collection:MongoCollection[Document] = db.getCollection("patron")
    collection.find(Filters.eq("patron_id","DTE6DAY1")).toFuture().map(seqdoc => seqdoc(0)).map(document => AuthenticationSuccessResponse(document.))
  }
}