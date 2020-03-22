package com.skeleton.service.user

object UserModel {

  case class User(id: Option[Int] = None,
                  userId: String,
                  email: String,
                  firstName: String,
                  lastName: String,
                  role: String)


  case class UserDto(userId: String,
                     email: String,
                     firstName: String,
                     lastName: String,
                     role: String)

  case class UserCreate(email: String,
                        firstName: String,
                        lastName: String,
                        //                        password: String,
                        role: String
                       )

  case class UpdateUser(
                         userId: Option[String],
                         //                         password: Option[String],
                         email: Option[String],
                         firstName: Option[String],
                         lastName: Option[String],
                         role: Option[String]
                       )

  case class Token(token: String, expiresIn: Int)

  def updateUserToUser(userId: String, updateUser: UpdateUser): User =
    User(
      userId = updateUser.userId.getOrElse(userId),
      email = updateUser.email.getOrElse(""),
      //      password = updateUser.password.getOrElse(""),
      firstName = updateUser.firstName.getOrElse(""),
      lastName = updateUser.lastName.getOrElse(""),
      role = updateUser.role.getOrElse("")
    )

  def updateUserRow(old: User, update: UpdateUser): User =
    old.copy(
      email = update.email.getOrElse(old.email),
      firstName = update.firstName.getOrElse(old.firstName),
      lastName = update.lastName.getOrElse(old.lastName),
      //      password = update.password.getOrElse(old.password),
      role = update.role.getOrElse(old.role)
    )

  implicit def userToUserDto(user: User): UserDto = {
    UserDto(user.userId, user.email, user.firstName, user.lastName, user.role)
  }

}
