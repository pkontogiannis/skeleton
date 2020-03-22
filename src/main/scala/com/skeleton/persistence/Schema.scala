package com.skeleton.persistence

import com.skeleton.service.user.persistence.UserTableDef


trait Schema extends SlickJdbcProfile
  with UserTableDef {

  import profile.api._

  implicit lazy val Users: TableQuery[UserTable] = TableQuery[UserTable]

  lazy val Schema: profile.DDL =
    Users.schema
}

