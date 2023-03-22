package lichess

import service.ErrorInfo

import scala.concurrent.Future
import sttp.client3.{HttpClientSyncBackend, Identity, SttpBackend, UriContext, basicRequest}


class LichessService(lichessUrl: String) {

  implicit val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  // part of logic for updateUserEndpoint
  def getUser(login: String): Future[Either[ErrorInfo, LichessUser]] = {
    val request = basicRequest.get(uri"$lichessUrl/api/user/$login")
    val response = request.send(backend)

    response.body match {
      case Right(json) => Future.successful(Decoders.decodeUserFromLichessJson(json))
      case Left(_) => Future.successful(Left(ErrorInfo.BadGateway("Lichess does not respond")))
    }
  }
}

