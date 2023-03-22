package db

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._


class PlayersTableSchema(tag: Tag) extends Table[Player](tag, "USERS") {

  def user = column[String]("NAME", O.PrimaryKey)
  def score = column[Int]("SCORE")

  override def * = (user, score) <> (Player.tupled, Player.unapply)
}

object PlayersTableSchema {
  lazy val usersTable = TableQuery[PlayersTableSchema]
  def createUserTableIfNotExists(db: PostgresProfile.backend.Database) = db.run(usersTable.schema.createIfNotExists)
}
