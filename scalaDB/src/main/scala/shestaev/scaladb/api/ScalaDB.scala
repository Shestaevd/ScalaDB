package shestaev.scaladb.api

import shestaev.scaladb.context.DBData
import shestaev.scaladb.entity.DBEntity

import scala.collection.mutable

class ScalaDB(dbPath: String) {

  private val dbData: DBData = DBData(dbPath, new mutable.LinkedHashMap())

  def :+[T <: DBEntity](value: T): ScalaDB = {
    ???
  }

  def :-[T <: DBEntity](value: T): ScalaDB = {
    ???
  }

  def get[T <: DBEntity](key: String): T = {
    ???
  }


  def update[T <: DBEntity](value: T): ScalaDB = :+(value)

  def delete[T <: DBEntity](value: T): ScalaDB = :-(value)

  def clear: ScalaDB = ???

  def find[T <: DBEntity](predicate: T => Boolean): Option[T] = {
    ???
  }

  def map[T <: DBEntity, R<: DBEntity](mapFunc: T => R): ScalaDB = {
    ???
  }

  def mapCollect[T <: DBEntity, R](mapFunc: T => R): Seq[R] = {
    ???
  }

  def collect: Seq[DBEntity] = {
    ???
  }

  def collectOnly[T <: DBEntity](predicate: T => Boolean): Seq[T] = {
    ???
  }

  def apply(entities: DBEntity*): DBEntity = ???

  def isEmpty: Boolean = dbData.data.isEmpty

  def length: Int = dbData.data.size
}

object ScalaDB {

  def apply(dbPath: String): ScalaDB = new ScalaDB(dbPath)

}