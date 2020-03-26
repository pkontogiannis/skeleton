package com.skeleton.service.user

import java.util.UUID

import scala.language.implicitConversions

object UserModel {

  def updateUserToUser(userId: UUID, updateUser: UpdateUser): User =
    User(
      userId = updateUser.userId.getOrElse(userId),
      email = updateUser.email.getOrElse(""),
      password = updateUser.password.getOrElse(""),
      firstName = updateUser.firstName.getOrElse(""),
      lastName = updateUser.lastName.getOrElse(""),
      role = updateUser.role.getOrElse("")
    )

  def updateUserRow(old: User, update: UpdateUser): User =
    old.copy(
      email = update.email.getOrElse(old.email),
      firstName = update.firstName.getOrElse(old.firstName),
      lastName = update.lastName.getOrElse(old.lastName),
      password = update.password.getOrElse(old.password),
      role = update.role.getOrElse(old.role)
    )

  case class User(id: Option[Int] = None,
                  userId: UUID,
                  email: String,
                  password: String,
                  firstName: String,
                  lastName: String,
                  role: String)

  case class UserDto(userId: UUID,
                     email: String,
                     firstName: String,
                     lastName: String,
                     role: String)

  case class UserCreate(email: String,
                        firstName: String,
                        lastName: String,
                        password: String,
                        role: String
                       )

  case class UpdateUser(
                         userId: Option[UUID],
                         password: Option[String],
                         email: Option[String],
                         firstName: Option[String],
                         lastName: Option[String],
                         role: Option[String]
                       )

  case class UserLogin(email: String, password: String)

  case class UserLoginDto(email: String, accessToken: Token, refreshToken: Token,
                          role: String, tokenType: String)

  case class Token(token: String, expiresIn: Int)

  implicit def userToUserDto(user: User): UserDto = {
    UserDto(user.userId, user.email, user.firstName, user.lastName, user.role)
  }


}
