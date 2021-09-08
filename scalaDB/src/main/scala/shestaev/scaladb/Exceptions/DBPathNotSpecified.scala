package shestaev.scaladb.Exceptions

case class DBPathNotSpecified() extends Exception {
  override def getMessage: String = "DB path is null"
  override def getLocalizedMessage: String = "You have to specify a path where you would like to store your data"
}
