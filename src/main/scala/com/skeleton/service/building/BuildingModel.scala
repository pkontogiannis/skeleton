package com.skeleton.service.building

import java.sql.Timestamp


object BuildingModel {

  case class Building(
                       id: Option[Int] = None,
                       buildingId: String,
                       address: String,
                       postalArea: String,
                       country: String,
                       geolocation: String,
                       buildingRegulation: Int,
                       createdOn: Timestamp
                     )


  case class BuildingAdmin(
                            userId: Int,
                            buildingId: Int,
                            since: Timestamp,
                            percentage: Double,
                            adminType: String
                          )


  case class BuildingDto(
                          buildingId: String,
                          address: String,
                          postalArea: String,
                          country: String,
                          geolocation: String,
                          createdOn: Timestamp
                        )

  case class BuildingCreate(
                             address: String,
                             postalArea: String,
                             country: String,
                             geolocation: String,
                             buildingRegulation: Int
                           )

  case class BuildingUpdate(
                             address: Option[String],
                             postalArea: Option[String],
                             country: Option[String],
                             geolocation: Option[String],
                             buildingRegulation: Option[Int]
                           )

  implicit def buildingToBuildingDto(building: Building): BuildingDto =
    BuildingDto(
      building.buildingId,
      building.address,
      building.postalArea,
      building.country,
      building.geolocation,
      building.createdOn
    )
}
