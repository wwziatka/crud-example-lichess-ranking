import service.{ErrorInfo, PlayerService}
import Utils.{player1, player3}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter




class PlayerEndpointsSpec extends AnyFlatSpec with Matchers with ScalatestRouteTest with MockFactory {


  lazy val mockPlayersService: PlayerService = mock[PlayerService]

  lazy val playersDbEndpoints = new PlayerEndpoints(mockPlayersService)

  lazy val routes: Route = AkkaHttpServerInterpreter().toRoute(playersDbEndpoints.all)


  behavior of "getPlayerEndpoint"
  it should "return message about a player" in {

    (mockPlayersService.getPlayer _).expects("user").returning(Future.successful(Right(player1))).once()

    Get("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual """{"user":"user1","score":500}"""
    }
  }

  it should "return message that an internal server error occurred" in {

    (mockPlayersService.getPlayer _).expects("user").returning(Future.successful(Left(ErrorInfo.InternalError("")))).once()

    Get("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that a not found player error occurred" in {

    (mockPlayersService.getPlayer _).expects("user").returning(Future.successful(Left(ErrorInfo.UserNotFound("")))).once()

    Get("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }


  behavior of "getBestPlayersEndpoint"
  it should "return message about players, does not take parameters" in {

    (mockPlayersService.getBestPlayers _).expects(0,1).returning(Future.successful(Right(List(player1)))).once()

    Get("lichess-ranking") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual """[{"user":"user1","score":500}]"""
    }
  }

  it should "return message about players, takes only score parameter" in {

    (mockPlayersService.getBestPlayers _).expects(100, 1).returning(Future.successful(Right(List(player1)))).once()

    Get("lichess-ranking/?score=100") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual """[{"user":"user1","score":500}]"""
    }
  }

  it should "return message about players, takes only limit parameter" in {

    (mockPlayersService.getBestPlayers _).expects(0, 2).returning(Future.successful(Right(List(player1, player3)))).once()

    Get("lichess-ranking/?limit=2") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual """[{"user":"user1","score":500},{"user":"user3","score":400}]"""
    }
  }

  it should "return message about players, takes both score and limit parameters" in {

    (mockPlayersService.getBestPlayers _).expects(100, 2).returning(Future.successful(Right(List(player1, player3)))).once()

    Get("lichess-ranking/?score=100&limit=2") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual """[{"user":"user1","score":500},{"user":"user3","score":400}]"""
    }
  }

  it should "return message that a not found error occurred" in {

    (mockPlayersService.getBestPlayers _).expects(0,1).returning(Future.successful(Left(ErrorInfo.UserNotFound("")))).once()

    Get("lichess-ranking") ~> routes ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that an internal error occurred" in {

    (mockPlayersService.getBestPlayers _).expects(0,1).returning(Future.successful(Left(ErrorInfo.InternalError("")))).once()

    Get("lichess-ranking") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }


  behavior of "deletePlayerFromDb"
  it should "deleted player and return a message that player has been deleted" in {

    (mockPlayersService.deletePlayer _).expects("user").returning(Future.successful(Right("deleted"))).once()

    Delete("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual "deleted"
    }
  }

  it should "return message that an internal error occurred" in {

    (mockPlayersService.deletePlayer _).expects("user").returning(Future.successful(Left(ErrorInfo.InternalError("")))).once()

    Delete("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that a not found error occurred" in {

    (mockPlayersService.deletePlayer _).expects("user").returning(Future.successful(Left(ErrorInfo.UserNotFound("")))).once()

    Delete("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }


  behavior of "updatePlayerEndpoint"
  it should "update player and return a message that player has been updated" in {

    (mockPlayersService.updatePlayerScore _).expects("user").returning(Future.successful(Right("updated")))

    Put("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual "updated"
    }
  }

  it should "return message that a bad gateway error occurred" in {

    (mockPlayersService.updatePlayerScore _).expects("user").returning(Future.successful(Left(ErrorInfo.BadGateway(""))))

    Put("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.BadGateway
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that an internal error occurred" in {

    (mockPlayersService.updatePlayerScore _).expects("user").returning(Future.successful(Left(ErrorInfo.InternalError(""))))

    Put("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that a not found error occurred" in {

    (mockPlayersService.updatePlayerScore _).expects("user").returning(Future.successful(Left(ErrorInfo.UserNotFound(""))))

    Put("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }


  behavior of "createPlayerFromLichessEndpoint"
  it should "create player and return message that player has been created" in {

    (mockPlayersService.createPlayerFromLichess _).expects("user").returning(Future.successful(Right("created")))

    Post("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual "created"
    }
  }

  it should "return message that a bad gateway error occurred" in {

    (mockPlayersService.createPlayerFromLichess _).expects("user").returning(Future.successful(Left(ErrorInfo.BadGateway(""))))

    Post("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.BadGateway
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that a bad request error occurred" in {

    (mockPlayersService.createPlayerFromLichess _).expects("user").returning(Future.successful(Left(ErrorInfo.BadRequest(""))))

    Post("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that an internal error occurred" in {

    (mockPlayersService.createPlayerFromLichess _).expects("user").returning(Future.successful(Left(ErrorInfo.InternalError(""))))

    Post("lichess-ranking/user") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  behavior of "createPlayerEndpoint"
  it should "return message that player has been created" in {

    (mockPlayersService.createPlayer _).expects(*).returning(Future.successful(Right("created")))

    Post("lichess-ranking").withEntity(Utils.validJsonExample) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual "created"
    }
  }

  it should "return message that a bad request error occurred" in {

    (mockPlayersService.createPlayer _).expects(Utils.validJsonExample).returning(Future.successful(Left(ErrorInfo.BadRequest(""))))

    Post("lichess-ranking").withEntity(Utils.validJsonExample) ~> routes ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }

  it should "return message that an internal error occurred" in {

   (mockPlayersService.createPlayer _).expects(Utils.validJsonExample).returning(Future.successful(Left(ErrorInfo.InternalError(""))))

    Post("lichess-ranking").withEntity(Utils.validJsonExample) ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[String] shouldEqual """{"msg":""}"""
    }
  }
}
