package shestaev.scaladb.api.save

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxApplicativeId
import shestaev.scaladb.Exceptions.FileIsBlockedByAnotherPID
import shestaev.scaladb.IO.IOUtils.{IOImplicitEither, suspendIO}
import shestaev.scaladb.context.{DBData, DBFile, PID}
import shestaev.scaladb.entity.DBEntity
import shestaev.scaladb.fs.FSUnit.workWithFile
import shestaev.scaladb.fs.FileUnit.{createDirIfNotExist, writeToFile}

import java.io.File
import scala.annotation.tailrec

object Save {

  def putUnsafe(dbData: DBData)(value: DBFile[DBEntity]): Option[DBEntity] = put(dbData)(value).keepRight.unsafeRunSync()

  def put(dbData: DBData)(value: DBFile[DBEntity]): IO[Either[Throwable, DBEntity]] = {

    for {
      out <- writeToFS(1)(
        PID(),
        dbData.path,
        dbData.data.getOrElse(value.entity.primaryKey, value)
      ).map(_.map(_ => value.entity))
      _ <- dbData.data.put(value.entity.primaryKey, value).pure[IO]
    } yield out

  }

  private def writeToFS(delayIfBusy: Int)(pid: PID, dbPath: String, dbValue: DBFile[DBEntity]): IO[Either[Throwable, File]] =
    for {
      dir <- createDirIfNotExist(dbPath)
      out <- tryToWrite(pid)(delayIfBusy)(dir, dbValue)
    } yield out

  private def tryToWrite(pid: PID)(delayIfBusy: Int)(dbPath:  Either[Throwable, File], dbValue: DBFile[DBEntity]): IO[Either[Throwable, File]] =
    dbPath match {
      case left @ Left(_) => left.pure[IO]
      case Right(path) =>
        workWithFile(pid)(dbValue)(e => writeToFile(path, dbValue.dir.path, dbValue.dir.self, e)) match {
          case Left(error) =>
            error match {
              case _: FileIsBlockedByAnotherPID => suspendIO(delayIfBusy) >> tryToWrite(pid: PID)(delayIfBusy)(dbPath, dbValue)
              case err => Left(err).pure[IO]
            }
          case Right(value) => value
        }
    }
}
