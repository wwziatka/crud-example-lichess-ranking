import lichess.{LichessService, LichessUser, LichessUserScore}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import service.ErrorInfo



class LichessServiceSpec extends AnyFlatSpec with BeforeAndAfterAll with ScalaFutures {

  val Port = 8089
  val Host = "localhost"
  val wireMockServer = new WireMockServer(wireMockConfig().port(Port))
  lazy val lichessUsersService = new LichessService(s"http://$Host:$Port")


  override def beforeAll() {
    wireMockServer.start()
    WireMock.configureFor(Host, Port)
  }

  override def afterAll() {
    wireMockServer.stop()
  }

  behavior of "getUser_method"
  it should "return LichessUser" in {

    // given
    val path = "/api/user/user"

    stubFor(get(urlEqualTo(path))
      .willReturn(aResponse()
        .withBody(Utils.validJsonExample)))

    // when
    val result = lichessUsersService.getUser("user").futureValue

    // then
    assert(result === Right(LichessUser("user",LichessUserScore(999))))
  }

  it should "return message that Decoding Failure occurred" in {

    // given
    val path = "/api/user/user1"

    stubFor(get(urlEqualTo(path))
      .willReturn(aResponse()
        .withBody(Utils.notValidJsonExample)))

    // when
    val result = lichessUsersService.getUser("user1").futureValue

    // then
    assert(result === Left(ErrorInfo.InternalError("DecodingFailure at .username: Missing required field")))
  }


  it should "return message that Error for Lichess occurred" in {

    // given
    val path = "/api/user/user1"

    stubFor(get(urlEqualTo(path))
      .willReturn(aResponse()
        .withStatus(500)))

    // when
    val result = lichessUsersService.getUser("user1").futureValue

    // then
    assert(result === Left(ErrorInfo.BadGateway("Lichess does not respond")))
  }
}


