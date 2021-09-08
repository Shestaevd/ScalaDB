package shestaev.scaladb.Exceptions

case class NotADirectory() extends Exception {
  override def getMessage: String = "DB path is not a directory"
  override def getLocalizedMessage: String = "You have to provide a path that should point to the db directory"
}
