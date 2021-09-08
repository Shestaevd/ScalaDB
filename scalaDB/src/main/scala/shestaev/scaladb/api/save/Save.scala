package shestaev.scaladb.api.save

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxApplicativeId
import shestaev.scaladb.IO.IOUtils.{IOImplicitEither, suspendIO}
import shestaev.scaladb.context.{Blocking, DBData, DBFile, Free, PID}
import shestaev.scaladb.entity.DBEntity
import shestaev.scaladb.fs.FileUnit.{createDirIfNotExist, writeToFile}

import java.io.File

object Save {

  def putUnsafe(dbData: DBData)(value: DBFile[DBEntity]): Option[DBEntity] = put(dbData)(value).keepRight.unsafeRunSync()

  def put(dbData: DBData)(entity: DBFile[DBEntity]): IO[Either[Throwable, DBEntity]] = {

    for {
      value <- blockFileToChange(2)(dbData)(entity)
      out <- writeToFS(
        dbData.path,
        dbData.data.getOrElse(value.entity.primaryKey, value)
      ).map(_.map(_ => value.entity))
      _ <- dbData.data.put(value.entity.primaryKey, value).pure[IO]
    // _ <- разблокировать
    } yield out

  }

  private def blockFileToChange(suspendIfBlocked: Int)(dbData: DBData)(target: DBFile[DBEntity]): IO[DBFile[DBEntity]] = {
    dbData.data.get(target.entity.primaryKey).fold {
      dbData.data.put(target.entity.primaryKey, target.block(PID()))
      target.pure[IO]
    } {
      case _: Free[DBEntity] =>
        dbData.data.put(target.entity.primaryKey, target.block(PID()))
        target.pure[IO]
      case _: Blocking[DBEntity] =>
        suspendIO(suspendIfBlocked) >> blockFileToChange(suspendIfBlocked)(dbData)(target)
    }
  }

  private def writeToFS(dbPath: String, dbValue: DBFile[DBEntity]): IO[Either[Throwable, File]] =
    for {
      dir <- createDirIfNotExist(dbPath)
      out <- dir match {
        case left @ Left(_) => left.pure[IO]
        case Right(path) => writeToFile(path, dbValue)
      }
    } yield out
}
