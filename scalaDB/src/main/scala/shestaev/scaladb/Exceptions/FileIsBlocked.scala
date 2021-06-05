package shestaev.scaladb.Exceptions

import shestaev.scaladb.entity.DBEntity

class FileIsBlocked(val process: String, val retries: Int = 0, val entity: DBEntity) extends Exception {
  override def getMessage: String = s"File is blocked by $process process: retries: $retries"
  override def getLocalizedMessage: String = s"File is currently blocked by another process of $process. Number of retries: $retries"
}
