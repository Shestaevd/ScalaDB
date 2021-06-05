package shestaev.scaladb

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import shestaev.scaladb.context.DBFile
import shestaev.scaladb.entity.DBEntity
import shestaev.scaladb.fs.FileUnit.writeToFile

import scala.collection.mutable

object Launcher extends IOApp {

  val linkedHashMap: mutable.Map[String, DBEntity] = mutable.LinkedHashMap.empty[String, DBEntity]

  override def run(args: List[String]): IO[ExitCode] = {

    IO(ExitCode.Success)
  }

  def hah(map: mutable.Map[String, Int]): Unit = {
    map.addOne("damn" -> 4)
  }


}
