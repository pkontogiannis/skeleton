package com.skeleton.service

import akka.actor.ActorSystem
import com.skeleton.service.auth.{ AuthService, AuthServiceDefault }
import com.skeleton.service.user.persistence.UserPersistenceSQL
import com.skeleton.service.user.{ UserService, UserServiceDefault }
import com.skeleton.utils.config.Configuration
import com.skeleton.utils.database.DBAccess

case class Dependencies(userService: UserService, authService: AuthService)

object Dependencies {

  def fromConfig(configuration: Configuration)(implicit system: ActorSystem): Dependencies = {

    val dbAccess = DBAccess(system)

    val userPersistence = new UserPersistenceSQL(dbAccess)

    val userService = new UserServiceDefault(userPersistence)
    val authService = new AuthServiceDefault(userPersistence)

    Dependencies(userService, authService)
  }
}
