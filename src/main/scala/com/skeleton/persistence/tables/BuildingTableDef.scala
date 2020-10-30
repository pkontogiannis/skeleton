package com.skeleton.persistence.tables

import java.sql.Timestamp

import com.skeleton.persistence.SlickJdbcProfile
import com.skeleton.service.building.BuildingModel.Building
import slick.lifted.ProvenShape

trait BuildingTableDef {

  self: SlickJdbcProfile =>

  import profile.api._

  class BuildingTable(tag: Tag) extends Table[Building](tag, Some("skeleton"), "building") {

    def * : ProvenShape[Building] =
      (
        id.?,
        buildingId,
        address,
        postalArea,
        country,
        geolocation,
        buildingRegulation,
        createdOn
      ) <> ((Building.apply _).tupled, Building.unapply)

    def id: Rep[Int] = column[Int]("building_id", O.PrimaryKey, O.AutoInc)

    def buildingId: Rep[String] = column[String]("building", O.Unique)

    def address: Rep[String] = column[String]("address", O.Unique)

    def postalArea: Rep[String] = column[String]("postal_area")

    def country: Rep[String] = column[String]("country")

    def geolocation: Rep[String] = column[String]("geolocation")

    def buildingRegulation: Rep[Int] = column[Int]("building_regulation")

    def createdOn: Rep[Timestamp] = column[Timestamp]("created_on")

  }

}
