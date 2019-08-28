package org.bheaver.ngl4.aa.authentication.datastore

import org.bheaver.ngl4.aa.db.DBConnection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
trait PatronDS {
  val collection = "patron"
  def findByCredentials(username: String, libCode: String, password: String): Future[Option[Document]]
}

class PatronDSImpl extends PatronDS{
  override def findByCredentials(username: String, libCode: String, password: String): Future[Option[Document]] = {
    val mongoDatabase:MongoDatabase = DBConnection(libCode)
    mongoDatabase.getCollection(collection).find(
      and(or(equal("patron_id",username),equal("email",username)),
        equal("user_password",password)
      )
    ).toFuture().map(documents => if(documents.size>0)Some(documents(0)) else None)
  }
}