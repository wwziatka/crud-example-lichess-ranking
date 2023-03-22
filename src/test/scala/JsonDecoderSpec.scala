import lichess.Decoders
import service.ErrorInfo
import Utils.{lichessUser, notValidJsonExample, validJsonExample}

import org.scalatest.flatspec.AnyFlatSpec


class JsonDecoderSpec extends AnyFlatSpec {

  behavior of "json decoder"
  it should "return a lichessUser" in {

    assert(Decoders.decodeUserFromLichessJson(validJsonExample) == Right(lichessUser))
  }

  it should "return an internalError" in {

    assert(Decoders.decodeUserFromLichessJson(notValidJsonExample) ==
      Left(ErrorInfo.InternalError("DecodingFailure at .username: Missing required field")))
  }

}
