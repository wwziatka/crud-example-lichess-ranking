import db.Player
import lichess.{LichessUser, LichessUserScore}

object Utils {

  val player0: Player = Player("user0", 999)
  val player1: Player = Player("user1", 500)
  val player2: Player = Player("user2", 300)
  val player3: Player = Player("user3", 400)
  val player1AfterUpdated: Player = Player("user1", 999)


  val lichessUser: LichessUser = LichessUser("user",LichessUserScore(999))


  val notValidJsonExample =
    """{
       "id":"user",
       "perfs":{
         "chess960": {"games":2,"rating":1381,"rd":251,"prog":0,"prov":true},
         "blitz":{"games":636,"rating":1268,"rd":45,"prog":-15},
         "streak":{"runs":3,"score":12},
         "puzzle":{"games":1216,"rating":1697,"rd":75,"prog":0},
         "atomic":{"games":2,"rating":1364,"rd":327,"prog":0,"prov":true},
         "bullet":{"games":431,"rating":1175,"rd":50,"prog":-16},
         "correspondence":{"games":1,"rating":1337,"rd":321,"prog":0,"prov":true},
         "classical":{"games":3,"rating":1118,"rd":224,"prog":0,"prov":true},
         "rapid":{"games":766,"rating":1734,"rd":65,"prog":60}
       },
       "createdAt":1596624655975,
       "profile":{"country":"PL","location":"NS"},
       "seenAt":1669216797106,
       "playTime":{"total":735082,"tv":0},
       "url":"https://lichess.org/@/user0",
       "count":{"all":1967,"rated":999, "ai":21,"draw":64,"drawH":63,"loss":947,"lossH":933,"win":956,"winH":950,"bookmark":0,"playing":0,"import":0,"me":0}
    }"""

  val validJsonExample =
    """{
       "id":"user",
       "username":"user",
       "perfs":{
         "chess960": {"games":2,"rating":1381,"rd":251,"prog":0,"prov":true},
         "blitz":{"games":636,"rating":1268,"rd":45,"prog":-15},
         "streak":{"runs":3,"score":12},
         "puzzle":{"games":1216,"rating":1697,"rd":75,"prog":0},
         "atomic":{"games":2,"rating":1364,"rd":327,"prog":0,"prov":true},
         "bullet":{"games":431,"rating":1175,"rd":50,"prog":-16},
         "correspondence":{"games":1,"rating":1337,"rd":321,"prog":0,"prov":true},
         "classical":{"games":3,"rating":1118,"rd":224,"prog":0,"prov":true},
         "rapid":{"games":766,"rating":1734,"rd":65,"prog":60}
       },
       "createdAt":1596624655975,
       "profile":{"country":"PL","location":"NS"},
       "seenAt":1669216797106,
       "playTime":{"total":735082,"tv":0},
       "url":"https://lichess.org/@/user1",
       "count":{"all":1967,"rated":999,"ai":21,"draw":64,"drawH":63,"loss":947,"lossH":933,"win":956,"winH":950,"bookmark":0,"playing":0,"import":0,"me":0}
    }"""
}
