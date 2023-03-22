# Lichess Ranking

This project is a simple REST API that implement CRUD operations for postgresql database, contained Lichess platform users ranking (a table with chosen players names with their scores). There is implemented a set of endpoints that allows to:
* put information about a player to ranking by the use of Lichess platform API
* put information about a player to ranking by the use of json file
* update information about a player score in ranking by the use of Lichess platform API
* get a player from ranking
* delete player from ranking


## Built With

The major libraries that are used in project:
* slick
* tapir
* sttp.client3
* akka.http

Other libraries:
* circe
* pureconfig

Libraries for tests:
* scalatest
* wiremock
* scalamock
* testcontainers


## Other information

Lichess platform -> open platform for chess players

