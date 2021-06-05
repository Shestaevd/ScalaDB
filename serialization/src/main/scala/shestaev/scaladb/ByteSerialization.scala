package shestaev.scaladb

import shestaev.scaladb.entity.DBEntity

import java.io._

object ByteSerialization extends Serialization {

  implicit val byteSerialization: ByteSerialization.type = ByteSerialization

  override def serialize[T <: DBEntity](obj: T): String = {
    val byteOut = new ByteArrayOutputStream()
    val objOut = new ObjectOutputStream(byteOut)
    objOut.writeObject(obj)
    objOut.close()
    byteOut.close()
    byteOut.toByteArray.map(_.toChar).mkString
  }

  override def deserialize[T <: DBEntity](bytes: String): T = {
    val byteIn = new ByteArrayInputStream(bytes.map(_.toByte).toArray)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[T]
    byteIn.close()
    objIn.close()
    obj
  }

}