package shestaev.scaladb

import shestaev.scaladb.entity.DBEntity

case class Foo(foo1: String, foo2: Int, foo3: Double) extends DBEntity {
  override def runtimeClass: Class[_] = Foo.getClass
}

object TestData {

  val foo: Foo = Foo("foo", 1 , 1d)
  val parsedJsonFoo = "{\"foo1\":\"foo\",\"foo2\":1,\"foo3\":1}"

}
