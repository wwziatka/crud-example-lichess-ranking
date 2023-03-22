package lichess

import service.ErrorInfo

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode


object Decoders {

  implicit val countNestedDecoder: Decoder[LichessUserScore] = deriveDecoder[LichessUserScore]
  implicit val lichessUserJsonDecoder: Decoder[LichessUser] = deriveDecoder[LichessUser]

  def decodeUserFromLichessJson(json: String): Either[ErrorInfo, LichessUser] = decode[LichessUser](json) match {
    case Right(json) => Right(json)
    case Left(er) => Left(ErrorInfo.InternalError(s"${er.getMessage}"))
  }
}
