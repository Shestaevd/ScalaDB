package shestaev.scaladb.entity

import java.util.UUID

trait Key[A] {
  val value: A
}

case class StringRandomKey(override val value: String = UUID.randomUUID().toString) extends Key[String] {
  override def toString: String = value
}