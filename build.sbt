lazy val root = (project in file("."))
  .settings(
    name := "lichess-ranking",
    version := "1.0",
    scalaVersion := "2.12.10"
  )

libraryDependencies ++= apiDocsDependencies ++ configDependencies ++ dbDependencies ++ forTestingDependencies ++ httpDependencies ++ jsonDependencies ++ serverDependencies

val circeVersion = "0.14.5"
val slickVersion = "3.4.1"
val tapirVersion = "1.2.10"
val testcontainersVersion = "0.40.12"
val akkaHttpVersion = "2.7.0"

val apiDocsDependencies = Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion
)

val configDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.2"
)

val dbDependencies = Seq(
  "org.postgresql" % "postgresql" % "42.5.4",
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
)

val forTestingDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersVersion % "test",
  "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersVersion % "test",
  "org.scalamock" %% "scalamock" % "5.2.0" % "test",
  "com.github.tomakehurst" % "wiremock" % "2.27.2" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaHttpVersion
)

val httpDependencies = Seq(
  "com.softwaremill.sttp.client3" %% "core" % "3.8.13",
  "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % tapirVersion
)

val jsonDependencies = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion
)

val serverDependencies = Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaHttpVersion
)


