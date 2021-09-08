package shestaev.scaladb.entity

import java.util.UUID

trait Key {
  val value: String
}

case class StringRandomKey(override val value: String = UUID.randomUUID().toString) extends Key {
  override def toString: String = value
}