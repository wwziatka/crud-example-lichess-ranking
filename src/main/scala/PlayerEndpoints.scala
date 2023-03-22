import db.Player
import io.circe.generic.auto._
import service.{ErrorInfo, PlayerService}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe._


class PlayerEndpoints(playersService: PlayerService) {

  private val baseEndpoint = endpoint.in("lichess-ranking")


  private val getPlayer = baseEndpoint
    .get
    .in(path[String]("login"))
    .out(jsonBody[Player])
    .errorOut(
      oneOf[ErrorInfo](
        oneOfVariant(StatusCode.InternalServerError, jsonBody[ErrorInfo.InternalError]),
        oneOfVariant(StatusCode.NotFound, jsonBody[ErrorInfo.UserNotFound]))
    )
    .serverLogic(playersService.getPlayer)


  private val getBestPlayers = baseEndpoint
    .get
    .in(query[Option[Int]]("score"))
    .in(query[Option[Int]]("limit"))
    .errorOut(oneOf[ErrorInfo](
      oneOfVariant(StatusCode.InternalServerError,jsonBody[ErrorInfo.InternalError]),
      oneOfVariant(StatusCode.NotFound, jsonBody[ErrorInfo.UserNotFound]))
    )
    .out(jsonBody[List[Player]])
    .serverLogic({
      case (Some(score), Some(limit)) => playersService.getBestPlayers(score,limit)
      case (Some(score), None) => playersService.getBestPlayers(score,1)
      case (None, Some(limit)) => playersService.getBestPlayers(0,limit)
      case (None, None) => playersService.getBestPlayers(0,1)
    })


  private val deletePlayer = baseEndpoint
    .delete
    .in(path[String]("login"))
    .errorOut(oneOf[ErrorInfo](
      oneOfVariant(StatusCode.InternalServerError,jsonBody[ErrorInfo.InternalError]),
      oneOfVariant(StatusCode.NotFound, jsonBody[ErrorInfo.UserNotFound]))
    )
    .out(stringJsonBody)
    .serverLogic(playersService.deletePlayer)


  private val updatePlayer = baseEndpoint
    .put
    .in(path[String]("login"))
    .errorOut(
      oneOf[ErrorInfo](
        oneOfVariant(StatusCode.BadGateway, jsonBody[ErrorInfo.BadGateway]),
        oneOfVariant(StatusCode.InternalServerError, jsonBody[ErrorInfo.InternalError]),
        oneOfVariant(StatusCode.NotFound, jsonBody[ErrorInfo.UserNotFound]))
    )
    .out(stringJsonBody)
    .serverLogic(playersService.updatePlayerScore)


  private val createPlayerFromLichess = baseEndpoint
    .post
    .in(path[String]("login"))
    .errorOut(
      oneOf[ErrorInfo](
        oneOfVariant(StatusCode.BadGateway, jsonBody[ErrorInfo.BadGateway]),
        oneOfVariant(StatusCode.BadRequest, jsonBody[ErrorInfo.BadRequest]),
        oneOfVariant(StatusCode.InternalServerError, jsonBody[ErrorInfo.InternalError]))
    )
    .out(stringJsonBody)
    .serverLogic(playersService.createPlayerFromLichess)


  private val createPlayer = baseEndpoint
    .post
    .in(stringBody)
    .errorOut(oneOf[ErrorInfo](
      oneOfVariant(StatusCode.BadRequest,jsonBody[ErrorInfo.BadRequest]),
      oneOfVariant(StatusCode.InternalServerError, jsonBody[ErrorInfo.InternalError]))
    )
    .out(stringJsonBody)
    .serverLogic(playersService.createPlayer)

  val all = List(getPlayer, getBestPlayers, createPlayerFromLichess, createPlayer, updatePlayer, deletePlayer)
}
