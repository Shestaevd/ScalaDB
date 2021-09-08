package shestaev.scaladb.entity

/**
 * WARN: Path is not contain the dbPath prefix
 * @param path class location inside of dbFolder
 * @param self full path inside of dbFolder to file
 */
private[shestaev] case class DBEntityPath (path: String, self: String)
