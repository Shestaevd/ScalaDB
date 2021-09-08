package shestaev.scaladb.context

import shestaev.scaladb.entity.DBEntity

import scala.collection.mutable

sealed trait Data[A] {
  val data: mutable.LinkedHashMap[String, A]
}

case class DBData(path: String, override val data: mutable.LinkedHashMap[String, DBFile[DBEntity]] =  mutable.LinkedHashMap.empty) extends Data[DBFile[DBEntity]]