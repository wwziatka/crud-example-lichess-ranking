import db.DbConfig

case class AppConfig(postgres: DbConfig, lichessBaseUrl: String)

