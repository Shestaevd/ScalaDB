package shestaev.scaladb.api

import shestaev.scaladb.Serialization
import shestaev.scaladb.context.DBData

import scala.collection.mutable

class ScalaDB(dbPath: String)(implicit serialization: Serialization) {
  private val dbData: DBData = DBData(new mutable.LinkedHashMap())
}

object ScalaDB {
  def apply(dbPath: String)(implicit serialization: Serialization): ScalaDB = new ScalaDB(dbPath)
}