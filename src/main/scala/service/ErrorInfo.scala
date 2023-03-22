package service


sealed trait ErrorInfo

object ErrorInfo {
  case class InternalError(msg: String) extends ErrorInfo // 500
  case class BadGateway(msg: String) extends ErrorInfo // 502
  case class BadRequest(msg: String) extends ErrorInfo // 400
  case class UserNotFound(msg: String) extends ErrorInfo //404
}

