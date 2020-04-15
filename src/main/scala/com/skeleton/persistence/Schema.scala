package com.skeleton.persistence

import com.skeleton.service.user.persistence.UserTableDef
import slick.lifted.TableQuery

trait Schema extends SlickJdbcProfile with UserTableDef {

  implicit lazy val Users: TableQuery[UserTable] = TableQuery[UserTable]

}
