package shestaev.scaladb.fs

import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import shestaev.scaladb.Exceptions.{FileIsBlockedByAnotherPID, FileIsNotBlockedForChanges}
import shestaev.scaladb.context.{Blocking, DBFile, Free, PID}
import shestaev.scaladb.entity.DBEntity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[shestaev] object FSUnit {

  def workWithFile[A]
    (pid: PID)
    (dbResource: DBFile[DBEntity])
    (func: DBEntity => A): Either[Throwable, A] =
      dbResource match {
        case Blocking(thisPid, entity, _) => if (thisPid.equalPid(pid)) func(entity).asRight[Throwable] else FileIsBlockedByAnotherPID().asLeft[A]
        case Free(_ ,_) => FileIsBlockedByAnotherPID().asLeft[A]
      }

  def inSource[A, B](data: Seq[A])(func: A => B): IO[Seq[B]] =
    IO.fromFuture(IO(Future.traverse(data)(value => Future(func(value)))))

  def handleDBDataInSource[Out]
    (pid: PID)
    (dbData: Seq[DBFile[DBEntity]])
    (prepare: DBFile[DBEntity] => Unit)
    (func: DBEntity => Out)
    (closing: Either[Throwable, Out] => Unit): IO[Seq[Either[Throwable, Out]]] =
      for {
        _ <- inSource(dbData)(prepare)
        out <- inSource(dbData)(workWithFile(pid)(_)(func))
        _ <- inSource(out)(closing)
      } yield out

}
