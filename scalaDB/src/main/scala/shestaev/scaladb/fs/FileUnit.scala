package shestaev.scaladb.fs

import cats.effect.IO
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId, catsSyntaxOptionId, toTraverseOps}
import shestaev.scaladb.ByteSerialization
import shestaev.scaladb.Exceptions.NotADirectory
import shestaev.scaladb.context.DBFile
import shestaev.scaladb.entity.DBEntity
import shestaev.scaladb.utlis.FileUtils._
import shestaev.scaladb.IO.IOUtils.IOImplicitEither

import java.io.{File, FileInputStream, FileOutputStream}

private[shestaev] object FileUnit {

  def deleteDir(dir: File): IO[Option[Throwable]] =
    if (dir.isDirectory) {
      for {
        errOpt <- dir
          .listFiles()
          .pure[IO]
          .flatMap(_.toList
            .traverse(file => if (file.isDirectory) deleteDir(file) else deleteFile(file))
            .map(errList => if (errList.nonEmpty) errList.headOption.flatten else None)
          )
        _ <- dir.delete().pure[IO]
      } yield errOpt
    } else {
      new NotADirectory()
        .some
        .pure[IO]
    }

  def deleteFile(file: File): IO[Option[Throwable]] = IO(file).map(_.delete()).attempt.keepLeft

  def readDir(dir: File): IO[Either[Throwable, List[File]]] =
    dir
      .pure[IO]
      .flatMap {
        case dir if !dir.exists() || dir.isFile  =>
          new NotADirectory().asLeft[List[File]].pure[IO]
        case dir => dir
          .listFiles()
          .toList
          .traverse(file => if (file.isDirectory) readDir(file) else (file :: Nil).asRight.pure[IO])
          .map(_.flatMap(_.toOption).flatten.asRight)
      }

  def createDirIfNotExist(path: String): IO[Either[Throwable, File]] =
    new File(path)
      .pure[IO]
      .map(file => if(file.exists() && file.isDirectory) file else { file.mkdir(); file} ).attempt

  def writeToFile[A <: DBEntity](dbPath: File, dirPath: String, selfPath: String, entity: A): IO[Either[Throwable, File]] =
    for {
      _ <- new File(dbPath.getPath + dirPath)
        .pure[IO]
        .map(file => if (!file.exists()) file.mkdir())
      out <- new FileOutputStream(dbPath + / + selfPath)
        .pure[IO]
        .bracket(_.write(ByteSerialization.serialize(entity)).pure[IO])(_.flush().pure[IO])
        .as(new File(dbPath + / + selfPath))
        .attempt
    } yield out


  def readFromFile[A <: DBEntity](file: File): IO[Either[Throwable, DBFile[A]]] =
    new FileInputStream(file)
      .pure[IO]
      .flatMap(_.readAllBytes().pure[IO])
      .flatMap(ByteSerialization.deserialize[A](_).pure[IO])
      .flatMap(DBFile(_).pure[IO])
      .attempt

}
