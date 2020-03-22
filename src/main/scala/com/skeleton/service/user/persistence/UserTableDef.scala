package com.skeleton.service.user.persistence

import com.skeleton.persistence.SlickJdbcProfile
import com.skeleton.service.user.UserModel.User
import slick.lifted.ProvenShape


trait UserTableDef {
  self: SlickJdbcProfile =>

  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "Users") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def userId: Rep[String] = column[String]("user_id", O.Unique)

    def email: Rep[String] = column[String]("email", O.Unique)

    //    def password: Rep[String] = column[String]("password")

    def firstName: Rep[String] = column[String]("first_name")

    def lastName: Rep[String] = column[String]("last_name")

    def role: Rep[String] = column[String]("role")

    def * : ProvenShape[User] = (
      id.?,
      userId,
      email,
      //      password,
      firstName,
      lastName,
      role
    ) <> ((User.apply _).tupled, User.unapply)
  }

}
