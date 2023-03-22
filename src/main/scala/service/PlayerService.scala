package service

import db.{Player, PlayersTableSchema}
import lichess.{Decoders, LichessService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

class PlayerService(db: Database, lichessService: LichessService) {

  // logic for getUserEndpoint
  def getPlayer(login: String): Future[Either[ErrorInfo, Player]] =
    getPlayerFromDb(login)

  // logic for getBestUsersEndpoint
  def getBestPlayers(
      score: Int,
      limit: Int
  ): Future[Either[ErrorInfo, List[Player]]] = getBestPlayersFromDb(score, limit)


  // logic for deleteUserEndpoint
  def deletePlayer(login: String): Future[Either[ErrorInfo, String]] =
    deletePlayerFromDb(login)

  // logic for updateUserEndpoint
  def updatePlayerScore(login: String): Future[Either[ErrorInfo, String]] = {

    lichessService.getUser(login).flatMap {
      case Right(lichessUser) =>
        updatePlayerInDb(login, lichessUser.count.rated)
      case Left(error) => Future.successful(Left(error))
    }
  }

  // logic for createUserEndpoint
  def createPlayerFromLichess(login: String): Future[Either[ErrorInfo, String]] = {

    lichessService.getUser(login).flatMap {
      case Right(lichessUser) =>
        addPlayerToDb(Player(lichessUser.username, lichessUser.count.rated))
      case Left(error) => Future.successful(Left(error))
    }
  }

  // logic for createUserEndpointFromAttachedJson
  def createPlayer(
      json: String
  ): Future[Either[ErrorInfo, String]] = {

    Decoders.decodeUserFromLichessJson(json) match {
      case Left(_) =>
        Future.successful(
          Left(ErrorInfo.BadRequest("Attached json file is not valid"))
        )
      case Right(lichessUser) =>
        addPlayerToDb(Player(lichessUser.username, lichessUser.count.rated))
    }
  }

  private def getPlayerFromDb(
      login: String
  ): Future[Either[ErrorInfo, Player]] = {
    val query =
      PlayersTableSchema.usersTable.filter(_.user === login).result.headOption

    db.run(query)
      .map {
        case Some(user) => Right(user)
        case None =>
          Left(ErrorInfo.UserNotFound(s"Player $login not found in database"))
      }
  }

  private def getBestPlayersFromDb(score: Int, limit: Int): Future[Either[ErrorInfo, List[Player]]] = {
    val query = PlayersTableSchema.usersTable.filter(_.score > score).sorted(_.score.reverse).take(limit).result

    db.run(query).map(x => Right(x.toList))
  }

  private def deletePlayerFromDb(
      login: String
  ): Future[Either[ErrorInfo, String]] = {
    val query = PlayersTableSchema.usersTable.filter(_.user === login).delete

    db.run(query)
      .map {
        case 0 =>
          Left(ErrorInfo.UserNotFound(s"Player $login does not exist in db"))
        case _ => Right(s"Player $login deleted")
      }
  }

  private def updatePlayerInDb(
      login: String,
      score: Int
  ): Future[Either[ErrorInfo, String]] = {
    val query = PlayersTableSchema.usersTable
      .filter(_.user === login)
      .update(Player(login, score))

    db.run(query)
      .map {
        case 0 =>
          Left(ErrorInfo.UserNotFound(s"Player $login does not exist in db"))
        case _ => Right(s"Player $login updated, score: $score")
      }
  }

  private def addPlayerToDb(user: Player): Future[Either[ErrorInfo, String]] = {

    val queryTryGetPlayer = PlayersTableSchema.usersTable
      .filter(_.user === user.user)
      .result
      .headOption

    val query = PlayersTableSchema.usersTable += user

    db.run(queryTryGetPlayer)
      .flatMap {
        case None =>
          db.run(query)
            .map(_ =>
              Right(s"Player ${user.user} added to db, score: ${user.score}")
            )
        case _ =>
          Future(Left(ErrorInfo.UserNotFound(s"Player already exists in db")))
      }
  }
}
