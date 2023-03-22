import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import db.PlayersTableSchema.createUserTableIfNotExists
import db.PostgresDb
import lichess.LichessService
import service.PlayerService
import pureconfig._
import pureconfig.generic.auto._
import slick.jdbc.PostgresProfile
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.io.StdIn


object Main extends App{

  val config: AppConfig = ConfigSource.default.loadOrThrow[AppConfig]

  lazy val lichessUsersService = new LichessService(config.lichessBaseUrl)

  val db: PostgresProfile.backend.Database = PostgresDb.createDatabase(config.postgres)
  createUserTableIfNotExists(db)

  val playersService = new PlayerService(db,lichessUsersService)

  val playersDbEndpoints = new PlayerEndpoints(playersService)

  val swaggerEndpoints = SwaggerInterpreter().fromServerEndpoints(playersDbEndpoints.all, "My swagger", "21.37")


  implicit val actorSystem: ActorSystem = ActorSystem("lichess-ranking-system")

  import actorSystem.dispatcher

  val routes: Route = AkkaHttpServerInterpreter().toRoute(playersDbEndpoints.all ++ swaggerEndpoints)

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
  StdIn.readLine() //Await.result(Http().newServerAt("localhost", 8080).bindFlow(routes), 1.minutes)

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => actorSystem.terminate())
}
