package shestaev.scaladb.fs

import cats.effect.IO
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId, catsSyntaxOptionId, toTraverseOps}
import shestaev.scaladb.Exceptions.NotADirectory
import shestaev.scaladb.IO.IOUtils.IOImplicitEither
import shestaev.scaladb.Serialization
import shestaev.scaladb.context.DBFile
import shestaev.scaladb.entity.DBEntity
import shestaev.scaladb.utlis.FileUtils._

import java.io.{File, FileInputStream, FileOutputStream}

private[shestaev] object FileUnit {
  def deleteDir(dir: File): IO[Option[Throwable]] =
    if (dir.isDirectory) {
      IO(dir.listFiles())
        .flatMap(_.toList
            .traverse(file => if (file.isDirectory) deleteDir(file) else deleteFile(file))
            .map(errList => if (errList.nonEmpty) errList.headOption.flatten else None)
        ) <* IO(dir.delete())
    } else {
      IO(new NotADirectory().some)
    }

  def deleteFile(file: File): IO[Option[Throwable]] = IO(file).map(_.delete()).attempt.keepLeft

  def readDir(dir: File): IO[List[Either[Throwable, File]]] =
    if (dir.isDirectory) {
      IO(dir.listFiles())
        .flatMap(_.toList
          .traverse(file => if (file.isDirectory) readDir(file) else IO(file.asRight :: Nil))
          .map(_.flatten))
    } else {
      IO(new NotADirectory().asLeft :: Nil)
    }

  def writeToFile[A <: DBEntity](dbPath: String, dbFile: DBFile[A])(implicit serialization: Serialization): IO[Either[Throwable, File]] =
    IO(new File(dbPath + / + dbFile.dir.path)).map(file => if (!file.exists()) file.mkdir()) *>
    IO(new FileOutputStream(dbPath + / + dbFile.dir.self))
      .bracket(_.write(serialization.serialize(dbFile.entity).map(_.toByte).toArray).pure[IO])(_.flush().pure[IO])
      .as(new File(dbPath + / + dbFile.dir.self))
      .attempt

}
