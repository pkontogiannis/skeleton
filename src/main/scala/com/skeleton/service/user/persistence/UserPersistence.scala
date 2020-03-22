package com.skeleton.service.user.persistence

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.user.UserModel.{UpdateUser, User, UserCreate}

import scala.concurrent.Future

trait UserPersistence {

  def getUsers: Future[Either[DatabaseError, List[User]]]

  def getUser(userId: String): Future[Either[DatabaseError, User]]

  def createUser(data: UserCreate): Future[Either[DatabaseError, User]]

  def updateUser(userId: String, updateUser: UpdateUser): Future[Either[DatabaseError, User]]

  def updateUserPartially(userId: String, updateUser: UpdateUser): Future[Either[DatabaseError, User]]

  def deleteUser(userId: String): Future[Either[DatabaseError, Boolean]]

  def loginUser(email: String, password: String): Future[Either[DatabaseError, User]]

  def deleteAllUsers(): Future[Either[DatabaseError, Boolean]]

}
