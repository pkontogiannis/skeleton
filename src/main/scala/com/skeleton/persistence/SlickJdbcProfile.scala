package com.skeleton.persistence

import akka.actor.ActorSystem
import com.skeleton.utils.database.DBAccess
import slick.jdbc.JdbcProfile

trait SlickJdbcProfile {
  lazy val dbAccess: DBAccess   = DBAccess(actorSystem)
  lazy val profile: JdbcProfile = slick.jdbc.PostgresProfile
  val actorSystem: ActorSystem

}
