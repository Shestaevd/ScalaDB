package shestaev.scaladb.api.save

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import shestaev.scaladb.IO.IOUtils.{IOImplicitEither, suspendIO}
import shestaev.scaladb.context.{Blocking, DBData, DBFile, Free, PID}
import shestaev.scaladb.entity.DBEntity
import shestaev.scaladb.fs.FileUnit.{createDirIfNotExist, writeToFile}

import java.io.File

object Save {

  def putUnsafe(dbData: DBData)(value: DBFile[DBEntity]): Option[DBEntity] = put(dbData)(value).keepRight.unsafeRunSync()

  def put(dbData: DBData)(newValue: DBFile[DBEntity]): IO[Either[Throwable, DBEntity]] =
    for {
      oldValue <- blockFileToChange(2)(dbData)(newValue)
      ioResult <- writeToFS(dbData.path, newValue)
      out <- unblockFile(dbData)(newValue, oldValue)(ioResult)
    } yield out

  private def unblockFile(dbData: DBData)(target: DBFile[DBEntity], old: Option[DBFile[DBEntity]])(either: Either[Throwable, File]): IO[Either[Throwable, DBEntity]] =
    either.fold(error => {
      old.foreach(dbFile => dbData.data.put(dbFile.entity.primaryKey, dbFile.free))
      error.asLeft[DBEntity]
    }, _ => {
      dbData.data.put(target.entity.primaryKey, target.free)
      target.entity.asRight[Throwable]
    }).pure[IO]

  private def blockFileToChange(suspendIfBlocked: Int)(dbData: DBData)(target: DBFile[DBEntity]): IO[Option[DBFile[DBEntity]]] = {
    dbData.data.get(target.entity.primaryKey)
      .fold {
        IO(Option.empty[DBFile[DBEntity]])
      } {
      case free: Free[DBEntity] =>
        dbData.data.put(free.entity.primaryKey, free.block(PID()))
        free.pure[Option].pure[IO]
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
