import db.{DbConfig, PostgresDb}
import db.PlayersTableSchema.usersTable
import lichess.{LichessService, LichessUser, LichessUserScore}
import service.{ErrorInfo, PlayerService}
import Utils.{player0, player1, player1AfterUpdated, player2, player3}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.testcontainers.utility.DockerImageName

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._



class PlayerServiceSpec extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfterAll with ForAllTestContainer with ScalaFutures {

  lazy val lichessUsersService: LichessService = new LichessService("lichessUrl") {
    override def getUser(login: String): Future[Either[ErrorInfo, LichessUser]] = Future.successful(Right(LichessUser(login,LichessUserScore(999))))
  }

  override val container: PostgreSQLContainer = new PostgreSQLContainer(
    Some(DockerImageName.parse("postgres:alpine")),
    pgUsername = Some("username123"),
    pgPassword = Some("password")
  )

  lazy val dbConfig: DbConfig = DbConfig(container.jdbcUrl, "org.postgresql.Driver", "username123", "password", "disabled", true)
  lazy val db: PostgresProfile.backend.Database = PostgresDb.createDatabase(dbConfig)
  lazy val playersService = new PlayerService(db, lichessUsersService)

  override def beforeAll(): Unit = {

      Await.result(db.run(usersTable.schema.createIfNotExists),1.seconds)
  }

  override def beforeEach() {

      Await.result(db.run(usersTable.delete), 1.seconds)
      Await.result(db.run(usersTable ++= List(player1, player2, player3)), 1.seconds)
  }


  behavior of "getPlayer method"
  it should "return a player" in {

      val result = playersService.getPlayer(player1.user).futureValue
      result shouldBe Right(player1)
  }

  it should "return message that player not found in database" in {

    val result = playersService.getPlayer("user4").futureValue
    result shouldBe Left(ErrorInfo.UserNotFound("Player user4 not found in database"))
  }


  behavior of "getBestPlayers method"
  it should "return an list of players" in {

    val result = playersService.getBestPlayers(200,3).futureValue
    result shouldBe Right(List(player1,player3,player2))
  }

  it should "return players with scores >300" in {

    val result = playersService.getBestPlayers(300,2).futureValue
    result shouldBe Right(List(player1,player3))
  }

  it should "return six best players (all off players)" in {

    val result = playersService.getBestPlayers(100,6).futureValue
    result shouldBe Right(List(player1, player3, player2))
  }

  it should "return empty List" in {

    val result = playersService.getBestPlayers(2000, 1).futureValue
    result shouldBe Right(Nil)
  }

  it should "return the best player" in {

    val result = playersService.getBestPlayers(0,1).futureValue
    result shouldBe Right(List(player1))
  }


  behavior of "deleteUPlayer method"
  it should "return message that player has been deleted" in {

    val result = playersService.deletePlayer(player1.user).futureValue
    result shouldBe Right("Player user1 deleted")
  }

  it should "return message that player not exists in db, so it can not be deleted" in {

    val result = playersService.deletePlayer("user0").futureValue
    result shouldBe Left(ErrorInfo.UserNotFound("Player user0 does not exist in db"))
  }


  behavior of "updatePlayer method"
  it should "update player and return message that player has been updated" in {

    val result = playersService.updatePlayerScore(player1.user).futureValue

    val query = usersTable.filter(_.user === player1.user).result.headOption
    val resultFromDb = db.run(query).futureValue

    result shouldBe Right("Player user1 updated, score: 999")
    resultFromDb shouldBe Some(player1AfterUpdated)
  }

  it should "return message that there is no such player in db" in {

    val result = playersService.updatePlayerScore("user0").futureValue

    val query = usersTable.filter(_.user === "user0").result.headOption
    val resultFromDb = db.run(query).futureValue

    result shouldBe Left(ErrorInfo.UserNotFound(s"Player user0 does not exist in db"))
    resultFromDb shouldBe None
  }


  behavior of "createPlayerFromLichess method"
  it should "create player and return message that player has been created" in {

    val result = playersService.createPlayerFromLichess(player0.user).futureValue

    val query = usersTable.filter(_.user === player0.user).result.headOption
    val resultFromDb = db.run(query).futureValue

    result shouldBe Right("Player user0 added to db, score: 999")
    resultFromDb shouldBe Some(player0)
  }

  it should "return info that there such player already exist" in {

    val result = playersService.createPlayerFromLichess(player1.user).futureValue

    val query = usersTable.filter(_.user === player1.user).result.headOption
    val resultFromDb = db.run(query).futureValue

    result shouldBe Left(ErrorInfo.UserNotFound("Player already exists in db"))
    resultFromDb shouldBe Some(player1)
  }

  behavior of "createPlayer method"
  it should "return message that player has been added to db" in {

    val result = playersService.createPlayer(Utils.validJsonExample).futureValue
    result shouldBe Right("Player user added to db, score: 999")
  }

  it should "return message that attached json is invalid" in {

    val result = playersService.createPlayer(Utils.notValidJsonExample).futureValue
    result shouldBe Left(ErrorInfo.BadRequest("Attached json file is not valid"))
  }
}