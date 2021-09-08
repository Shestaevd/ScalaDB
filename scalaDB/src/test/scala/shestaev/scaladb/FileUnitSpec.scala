package shestaev.scaladb

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxApplicativeId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.must.Matchers.{exist, not}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import shestaev.scaladb.TestData.foo
import shestaev.scaladb.context.DBFile
import shestaev.scaladb.fs.FileUnit.{deleteDir, readDir, readFromFile, writeToFile}
import shestaev.scaladb.utlis.FileUtils./

import java.io.File

class FileUnitSpec extends AnyFlatSpec {

  lazy val testDir: String =  System.getProperty("user.home") + / + "testDir" + /

  private def createJunk(userDir: String): Unit = {

    def createDir(dirName: String): File = {
      val testDir = new File(dirName)
      if (!testDir.exists()) testDir.mkdir()
      testDir
    }

    def createFile(fileName: String): File = {
      val testFile = new File(fileName)
      testFile.createNewFile()
      testFile
    }

    createDir(userDir)

    for(i <- 1 to 10) createFile(userDir + / + i + ".txt")

    for (i <- 10 to 15) {
      createDir(userDir + / + i)
      for (j <- 1 to 10) {
        createFile(userDir + / + i + / + j + ".txt")
      }
    }
  }

  "Directory cleaning test" should "create dir, fill with some files, and delete them" in {

    val testDir = new File(this.testDir)

    val result = (IO(createJunk(this.testDir)) >> deleteDir(testDir)).unsafeRunSync()

    testDir should not (exist)
    result shouldBe None
  }

  "Reading directory test" should "create a dir of some junk and read it to array of files" in {
    val testDir = new File(this.testDir)

    val result = (
      for { _ <- IO(createJunk(this.testDir))
            out <- readDir(testDir)
            _ <- deleteDir(testDir)} yield out
      ).unsafeRunSync().toOption.get

    result should not be Matchers.empty
  }

  "Serialization test" should "write given object to file and read it back" in {

    val testDir = new File(this.testDir)

    val target = DBFile(foo)

   val fooBack = for {
    dir <- IO(if (!testDir.exists()) {
      testDir.mkdir()
      println("testDir has been created")
      testDir
    } else testDir)
    out <- writeToFile(dir, target).flatMap {
      case Left(error) => throw error
      case Right(file) => readFromFile[Foo](file).flatMap({
        case Left(error) => throw error
        case Right(obj) => obj.entity.pure[IO]
      })
    }
    _ <- deleteDir(testDir)
    } yield out

    fooBack.unsafeRunSync() shouldBe foo

  }


}
