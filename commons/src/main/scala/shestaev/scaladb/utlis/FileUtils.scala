package shestaev.scaladb.utlis

import shestaev.scaladb.entity.{DBEntity, DBEntityPath}



object FileUtils {

  lazy val / : String = System.getProperty("file.separator")

  def create[A <: DBEntity](obj: A): DBEntityPath =
    DBEntityPath(obj.name + /, obj.name + / + obj.primaryKey)

}