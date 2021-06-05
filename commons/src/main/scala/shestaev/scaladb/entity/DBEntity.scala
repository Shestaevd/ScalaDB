package shestaev.scaladb.entity

import scala.reflect.ClassTag

trait DBEntity extends Serializable with ClassTag[DBEntity] {
  val primaryKey: StringRandomKey = StringRandomKey() //temporary solution
  val name: String = runtimeClass.getSimpleName
}