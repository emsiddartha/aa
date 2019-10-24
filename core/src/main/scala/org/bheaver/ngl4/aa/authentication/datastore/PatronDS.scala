package org.bheaver.ngl4.aa.authentication.datastore

import org.bheaver.ngl4.util.db.DBConnection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.model.Filters._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.bheaver.ngl4.util.DateUtil._

trait PatronDS {
  val collection = "patron"

  def findByCredentials(username: String, libCode: String, password: String): Future[Option[Map[String, String]]]
}

class PatronDSImpl(dbConnection: DBConnection) extends PatronDS {
  override def findByCredentials(username: String, libCode: String, password: String): Future[Option[Map[String, String]]] = {
    val mongoDatabase: MongoDatabase = dbConnection.getDatabase(libCode)
    mongoDatabase.getCollection(collection).find(
      and(or(equal("patron_id", username), equal("email", username)),
        equal("user_password", password)
      )
    ).toFuture().map(documents => if (documents.size > 0) Some(Map("patron_id" -> documents(0).getString("patron_id"),
      "patron_category_id" -> documents(0).getInteger("patron_category_id").toString,
      "fname" -> documents(0).getString("fname"),
      "mname" -> documents(0).getString("mname"),
      "lname" -> documents(0).getString("lname"),
      "dept_id" -> documents(0).getInteger("dept_id").toString,
      "membership_start_date" -> legacyDateStringToDate(documents(0).getString("membership_start_date")).getTime.toString,
      "membership_end_date" -> legacyDateStringToDate(documents(0).getString("membership_expiry_date")).getTime.toString,
      "status" -> documents(0).getString("status"))) else None)
  }
}