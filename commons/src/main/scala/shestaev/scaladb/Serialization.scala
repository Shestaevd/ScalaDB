package shestaev.scaladb

import shestaev.scaladb.entity.DBEntity

trait Serialization {
  def serialize[A <: DBEntity](obj: A): String
  def deserialize[A <: DBEntity](serObj: String): A
}