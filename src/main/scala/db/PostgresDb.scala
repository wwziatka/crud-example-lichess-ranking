package db

import slick.jdbc


object PostgresDb {

  import slick.jdbc.PostgresProfile.api._

  def createDatabase(dbConfig: DbConfig): jdbc.PostgresProfile.backend.DatabaseDef = Database.forURL(dbConfig.url, dbConfig.user, dbConfig.password)
}
