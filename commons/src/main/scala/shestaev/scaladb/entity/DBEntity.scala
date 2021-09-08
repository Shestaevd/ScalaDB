package shestaev.scaladb.entity

trait DBEntity extends Serializable {
  val primaryKey: String = StringRandomKey().value // todo temporary solution
  val name: String = getClass.getSimpleName
}