package shestaev.scaladb

import org.scalatest.flatspec.AnyFlatSpec
import shestaev.scaladb.context.DBData
import shestaev.scaladb.utlis.FileUtils./

class SaveSpec extends AnyFlatSpec {

  lazy val testDir: String =  System.getProperty("user.home") + / + "testDir"

  val testDBData: DBData = DBData(testDir)

  "Api save test" should "save data to testDBData" in {
//    val foo = new Foo()
  }

}
