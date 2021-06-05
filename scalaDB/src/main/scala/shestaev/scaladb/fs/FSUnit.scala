package shestaev.scaladb.fs

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import cats.effect.IO
import shestaev.scaladb.Exceptions.FileIsBlocked
import shestaev.scaladb.context.{Blocking, DBData, Free}
import shestaev.scaladb.entity.DBEntity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[shestaev] object FSUnit {

  private def workWithFile[A, DBFile[DBEntity]]
  (process: String, retry: Int)
  (dbResource: DBFile[DBEntity])
  (func: Free[DBEntity] => A): IO[Either[Throwable, A]] =
    dbResource match {
      case free: Free[DBEntity]  => IO(func(free)).attempt
      case Blocking(entity, _) => IO(Left[Throwable, A](new FileIsBlocked(process, retry, entity)))
    }

  def inSource[A, B](threads: Int)(data: Seq[A])(func: A => B)(implicit system: ActorSystem = ActorSystem()): IO[Seq[B]] =
    IO.fromFuture(IO(Source(data).mapAsyncUnordered(threads)(a => Future(func(a))).runWith(Sink.seq[B])))

  def handleDBDataInSource[DBFile[DBEntity], Out]
  (threads: Int, process: String, retries: Int = 0)
  (dbData: Seq[DBFile[DBEntity]])
  (prepare: DBFile[DBEntity] => Unit)
  (ioFunc: Free[DBEntity] => Out)
  (closing: IO[Either[Throwable, Out]] => Unit)
  (implicit system: ActorSystem = ActorSystem()): IO[Seq[IO[Either[Throwable, Out]]]] = {
    val ioPrep = inSource(threads)(dbData)(prepare)
    val ioWork = ioPrep >> inSource(threads)(dbData)(workWithFile(process, retries)(_)(ioFunc))
    ioWork.flatMap(processedData => inSource(threads)(processedData)(closing)).as(ioWork).flatten
  }

}
