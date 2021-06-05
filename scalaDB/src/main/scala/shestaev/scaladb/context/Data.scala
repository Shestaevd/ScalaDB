package shestaev.scaladb.context

import shestaev.scaladb.entity.DBEntity

import scala.collection.mutable

sealed trait Data[A] {
  val data: mutable.LinkedHashMap[String, A] = mutable.LinkedHashMap.empty[String, A]
}

case class DBData(override val data: mutable.LinkedHashMap[String, DBFile[DBEntity]]) extends Data[DBFile[DBEntity]]