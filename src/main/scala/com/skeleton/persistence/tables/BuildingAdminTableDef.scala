package com.skeleton.persistence.tables

import java.sql.Timestamp

import com.skeleton.persistence.{Schema, SlickJdbcProfile}
import com.skeleton.service.building.BuildingModel
import com.skeleton.service.building.BuildingModel.BuildingAdmin
import com.skeleton.service.user.UserModel
import slick.lifted.{ForeignKeyQuery, ProvenShape}

trait BuildingAdminTableDef extends Schema {

  self: SlickJdbcProfile =>

  import profile.api._

  class BuildingAdminTable(tag: Tag) extends Table[BuildingAdmin](tag, Some("skeleton"), _tableName = "building_admin") {
    def * : ProvenShape[BuildingAdmin] = (userId, buildingId, since, percentage, adminType) <> ((BuildingAdmin.apply _).tupled, BuildingAdmin.unapply)

    def buildingId: Rep[Int] = column[Int]("building_id")

    def since: Rep[Timestamp] = column[Timestamp]("since")

    def percentage: Rep[Double] = column[Double]("percentage")

    def adminType: Rep[String] = column[String]("admin_type")

    def aFK: ForeignKeyQuery[UserTable, UserModel.User] = foreignKey("user_id", userId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)

    def userId: Rep[Int] = column[Int]("user_id")

    def bFK: ForeignKeyQuery[BuildingTable, BuildingModel.Building] = foreignKey("building_id", buildingId, Buildings)(_.id, onDelete = ForeignKeyAction.Cascade)
  }

}
