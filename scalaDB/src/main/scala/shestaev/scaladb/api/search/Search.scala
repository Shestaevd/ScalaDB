//package shestaev.scaladb.API.save.search
//
//import shestaev.scaladb.Serialization
//import shestaev.scaladb.context.SearchContext
//import shestaev.scaladb.entity.DBEntity
//
//object Search {
//  def apply[A <: DBEntity](dbPath: String, serialization: Serialization): Search[A] = new Search[A](dbPath, serialization)
//  def by[A <: DBEntity]: String => Serialization => (A => Boolean) => ExtendSearch[A] = dbPath => serialization => predicate =>
//    new ExtendSearch[A](new SearchContext[A](dbPath = dbPath, serialization = serialization, predicates = predicate :: Nil))
//  def withKey[A <: DBEntity]: String => Serialization => String => ExtendSearch[A] = dbPath => serialization => key =>
//    by(dbPath)(serialization)(_.primaryKey.value == key)
//  def all[A <: DBEntity]: String => Serialization => ExtendSearch[A] = dbPath => serialization =>
//    by(dbPath)(serialization)(_ => true)
//}
//
//sealed class Search[A <: DBEntity](dbPath: String, serialization: Serialization) {
//  def by: (A => Boolean) => ExtendSearch[A] = Search.by[A](dbPath)(serialization)
//  def withKey: String => ExtendSearch[A] = Search.withKey[A](dbPath)(serialization)
//  def all: ExtendSearch[A] = Search.all[A](dbPath)(serialization)
//}
//
//sealed class ExtendSearch[A <: DBEntity](context: SearchContext[A]) {
////  def async(threads: Int)(predicate: A => Boolean): ExtendSearch = Search.async(dbPath)(serialization)(threads)(predicate)
//}
//
//sealed class UniqueSearch