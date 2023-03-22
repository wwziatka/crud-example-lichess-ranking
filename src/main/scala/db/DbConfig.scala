package db


final case class DbConfig(url: String, driver: String, user: String, password: String, connectionPool: String, keepAliveConnection: Boolean)