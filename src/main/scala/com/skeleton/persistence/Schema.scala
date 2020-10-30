package com.skeleton.persistence

import com.skeleton.persistence.tables.{BuildingTableDef, UserTableDef}
import slick.lifted.TableQuery

trait Schema extends SlickJdbcProfile with UserTableDef with BuildingTableDef {

  implicit lazy val Users: TableQuery[UserTable] = TableQuery[UserTable]

  implicit lazy val Buildings: TableQuery[BuildingTable] = TableQuery[BuildingTable]
}
