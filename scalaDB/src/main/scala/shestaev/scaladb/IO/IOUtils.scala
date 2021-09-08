package shestaev.scaladb.IO

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxApplicativeId
import shestaev.scaladb.Exceptions.NotADirectory

import java.io.File
import java.io.FileInputStream

private[shestaev] object IOUtils {

  val suspendIO: Int => IO[Unit] = time => IO(Thread.sleep(time))

  implicit class IOImplicitString(io: IO[String]) {
    def toFile: IO[Either[Throwable, File]] = io.map(path => new File(path)).attempt

    def isDirectory: IO[Either[Throwable, File]] = io.toFile.map {
      case left: Left[Throwable, File]                     => left
      case right @ Right(resource) if resource.isDirectory => right
      case _                                               => Left(new NotADirectory())
    }
  }
  implicit class IOImplicitEitherFile(io: IO[Either[Throwable, File]]) {
    def isDirectory: IO[Either[Throwable, File]] = io.map {
      case left: Left[Throwable, File]                     => left
      case right @ Right(resource) if resource.isDirectory => right
      case _                                               => Left(new NotADirectory())
    }
  }

  implicit class IOImplicitEither[A, B](io: IO[Either[A, B]]) {
    def keepLeft: IO[Option[A]] = io.map(_.fold(Some(_), _ => None))

    def keepRight: IO[Option[B]] = io.map(_.fold(_ => None, Some(_)))
  }

}
