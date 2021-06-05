package shestaev.scaladb

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import shestaev.scaladb.TestData.foo
import shestaev.scaladb.entity.DBEntity

class ByteSerializationSpec extends AnyFlatSpec {

  "Bytes serialization test" should "serialize and deserialize back given value" in {

    val bytes = ByteSerialization.serialize(foo)

    val backToObj = ByteSerialization.deserialize[DBEntity](bytes)

    backToObj shouldBe foo
  }

}