package shestaev.scaladb.context

import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import shestaev.scaladb.Exceptions.DBPathNotSpecified
import shestaev.scaladb.IO.IOUtils.IOImplicitString

import java.io.File

object DBDataLoader {
  def pull(dbPath: String): Either[DBData, Throwable] = {
    IO.fromOption(Option(dbPath))(new DBPathNotSpecified).toFile
    ???
  }

  def push(dbPath: String)(dbData: DBData): Option[Throwable] = {
    IO.fromOption(Option(dbPath))(new DBPathNotSpecified).isDirectory.bracket {
      case Left(error) => IO(error.asLeft[File])
      case Right(file) => IO(file)
    }{
      ???
    }
    ???
  }
}
