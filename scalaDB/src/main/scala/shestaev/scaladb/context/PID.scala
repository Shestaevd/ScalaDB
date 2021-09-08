package shestaev.scaladb.context

import java.time.LocalDateTime
import java.util.UUID

case class PID (value: Long = UUID.randomUUID().node(), processStart: LocalDateTime = LocalDateTime.now()) {
  def equalPid(o: PID): Boolean = value == o.value && processStart.isEqual(o.processStart)
}
