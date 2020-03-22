package com.skeleton.service.user

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.errors.ServiceError.GenericDatabaseError
import com.skeleton.service.user.UserModel.{UpdateUser, UserCreate, UserDto}
import com.skeleton.service.user.persistence.UserPersistence
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceDefault(val userPersistence: UserPersistence)
  extends UserService with LazyLogging {

  import logger._

  def getUsers: Future[Either[DatabaseError, List[UserDto]]] =
    userPersistence.getUsers.map {
      case Right(value) =>
        info(s"[UserService] successfully retrieve a list of users with uuid: ${value.map(us => us.userId).mkString(", ")}")
        Right(value.map(user => UserModel.userToUserDto(user)))
      case Left(_) => Left(GenericDatabaseError)
    }

  def getUser(userId: String): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.getUser(userId).map {
      case Right(value) =>
        info(s"[UserService] successfully retrieve a user with uuid: ${value.userId}")
        Right(UserModel.userToUserDto(value))
      case Left(error) => Left(error)
    }
  }

  def createUser(userCreate: UserCreate): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.createUser(userCreate).map {
      case Right(value) =>
        info(s"[UserService] successfully created a user with uuid: ${value.userId}")
        Right(UserModel.userToUserDto(value))
      case Left(error) =>
        Left(error)
    }
  }

  def updateUser(userId: String, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.updateUser(userId, updateUser).map {
      case Right(value) =>
        info(s"[UserService] successfully update a user with uuid: ${value.userId}")
        Right(UserModel.userToUserDto(value))
      case Left(error) => Left(error)
    }
  }

  def updateUserPartially(userId: String, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]] =
    userPersistence.updateUserPartially(userId, updateUser).map {
      case Right(value) =>
        info(s"[UserService] successfully partially update a user with uuid: ${value.userId}")
        Right(UserModel.userToUserDto(value))
      case Left(error) => Left(error)
    }

  def deleteUser(userId: String): Future[Either[DatabaseError, Boolean]] =
    userPersistence.deleteUser(userId).map {
      case Right(value) =>
        info(s"[UserService] successfully delete a user with uuid: $userId")
        Right(value)
      case Left(error) => Left(error)
    }
}

