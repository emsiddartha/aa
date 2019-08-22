package org.bheaver.ngl4.aa.db

import org.mongodb.scala.{MongoClient, MongoDatabase}

object DBConnection {
  val mongoClient:MongoClient = MongoClient("mongodb://localhost")
  val database:MongoDatabase = mongoClient.getDatabase("lib1")

}
