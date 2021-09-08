package shestaev.scaladb

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import shestaev.scaladb.context.DBFile
import shestaev.scaladb.entity.DBEntity
import shestaev.scaladb.fs.FileUnit.writeToFile

import scala.collection.mutable

object Launcher extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

//    for { _ <- IO{println("")} } yield()

//    val program = for {
//      _ <- IO { println("Welcome to Scala!  What's your name?") }
//      name <- IO { Console.readLine }
//      _ <- IO { println(s"Well hello, $name!") }
//    } yield ()

    IO(ExitCode.Success)
  }


}
