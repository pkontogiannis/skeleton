package com.skeleton.service

import akka.actor.ActorSystem
import com.skeleton.service.auth.{ AuthService, AuthServiceDefault }
import com.skeleton.service.health.{ HealthService, HealthServiceDefault }
import com.skeleton.service.user.persistence.UserPersistenceSQL
import com.skeleton.service.user.{ UserService, UserServiceDefault }
import com.skeleton.utils.database.DBAccess

case class Dependencies(dbAccess: DBAccess, userService: UserService, authService: AuthService, healthService: HealthService)

object Dependencies {

  def fromConfig(implicit system: ActorSystem): Dependencies = {

    val dbAccess = DBAccess(system)

    val userPersistence = new UserPersistenceSQL(dbAccess)

    val userService   = new UserServiceDefault(userPersistence)
    val authService   = new AuthServiceDefault(userPersistence)
    val healthService = new HealthServiceDefault(dbAccess)

    Dependencies(dbAccess, userService, authService, healthService)
  }
}
