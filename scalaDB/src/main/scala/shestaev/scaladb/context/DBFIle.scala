package shestaev.scaladb.context

import shestaev.scaladb.entity.{DBEntity, DBEntityPath}
import shestaev.scaladb.utlis.FileUtils

sealed trait DBFile[A <: DBEntity] {

  val entity: A

  val dir: DBEntityPath

  def block: Blocking[A]

  def free: Free[A]

}

final case class Free[A <: DBEntity](override val entity: A, override val dir: DBEntityPath) extends DBFile[A] {

  override def block: Blocking[A] = Blocking(entity, dir)

  override def free: Free[A] = this
}

final case class Blocking[A <: DBEntity](override val entity: A, override val dir: DBEntityPath) extends DBFile[A] {

  override def block: Blocking[A] = this

  override def free: Free[A] = Free(entity, dir)

}

object DBFile {

  def apply[A <: DBEntity](x: A): DBFile[A] = Free(x, FileUtils.create(x))

  def blocking[A <: DBEntity](x: A): DBFile[A] = Blocking(x, FileUtils.create(x))

}