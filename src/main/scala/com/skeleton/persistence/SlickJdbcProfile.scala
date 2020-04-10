package com.skeleton.persistence

import akka.actor.ActorSystem
import slick.jdbc.JdbcProfile

trait SlickJdbcProfile {
  lazy val profile: JdbcProfile = slick.jdbc.PostgresProfile
  val actorSystem: ActorSystem
}
