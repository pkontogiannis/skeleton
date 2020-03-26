package com.skeleton.service.user

import java.util.UUID

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.user.UserModel.{UpdateUser, UserCreate, UserDto}

import scala.concurrent.Future

trait UserService {

  def getUsers: Future[Either[DatabaseError, List[UserDto]]]

  def getUser(userId: UUID): Future[Either[DatabaseError, UserDto]]

  def createUser(userCreate: UserCreate): Future[Either[DatabaseError, UserDto]]

  def updateUser(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]]

  def updateUserPartially(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]]

  def deleteUser(userId: UUID): Future[Either[DatabaseError, Boolean]]

}
